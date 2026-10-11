package com.agri.ai.portal.controller;

import com.agri.ai.portal.dto.SafeSprayRequest;
import com.agri.ai.portal.model.FieldProofRecord;
import com.agri.ai.portal.model.SafeSprayRecord;
import com.agri.ai.portal.repository.FieldProofRepository;
import com.agri.ai.portal.repository.SafeSprayRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Validated
@RestController
@RequestMapping("/api/portal")
public class CropSafetyController {
    private static final long MAX_PHOTO_BYTES = 5L * 1024 * 1024;
    private static final long MAX_PHOTO_PIXELS = 20_000_000L;
    private final FieldProofRepository fieldProofs;
    private final SafeSprayRepository sprayRecords;

    public CropSafetyController(FieldProofRepository fieldProofs, SafeSprayRepository sprayRecords) {
        this.fieldProofs = fieldProofs;
        this.sprayRecords = sprayRecords;
    }

    @PostMapping(value = "/fieldproof", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Map<String, Object> addFieldProof(
            @RequestParam @NotBlank String cropName,
            @RequestParam LocalDate incidentDate,
            @RequestParam @NotBlank String location,
            @RequestParam @NotBlank String incidentDetails,
            @RequestParam("photo") MultipartFile photo) {
        if (incidentDate.isAfter(LocalDate.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Incident date cannot be in the future.");
        }
        if (cropName.length() > 100 || location.length() > 120 || incidentDetails.length() > 2000) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Some incident details exceed the allowed length.");
        }
        if (photo.isEmpty() || photo.getSize() > MAX_PHOTO_BYTES) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Choose a JPG or PNG photo smaller than 5 MB.");
        }
        String contentType = photo.getContentType();
        if (!MediaType.IMAGE_JPEG_VALUE.equals(contentType) && !MediaType.IMAGE_PNG_VALUE.equals(contentType)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Upload a JPEG or PNG image.");
        }
        try {
            byte[] imageBytes = photo.getBytes();
            BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageBytes));
            if (image == null || (long) image.getWidth() * image.getHeight() > MAX_PHOTO_PIXELS) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The photo is invalid or has too many pixels.");
            }
            if ((MediaType.IMAGE_JPEG_VALUE.equals(contentType) && !hasJpegSignature(imageBytes))
                    || (MediaType.IMAGE_PNG_VALUE.equals(contentType) && !hasPngSignature(imageBytes))) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The uploaded file content does not match its image type.");
            }
            FieldProofRecord record = new FieldProofRecord();
            record.setCropName(cropName.trim());
            record.setIncidentDate(incidentDate);
            record.setLocation(location.trim());
            record.setIncidentDetails(incidentDetails.trim());
            record.setPhotoName(safeFilename(photo.getOriginalFilename()));
            record.setPhotoContentType(contentType);
            record.setPhotoData(imageBytes);
            record.setCreatedAt(Instant.now());
            return fieldProofView(fieldProofs.save(record));
        } catch (IOException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The uploaded photo could not be read.");
        }
    }

    @GetMapping("/fieldproof")
    public List<Map<String, Object>> fieldProofHistory() {
        return fieldProofs.findAllByOrderByIncidentDateDescCreatedAtDesc().stream()
                .map(this::fieldProofView)
                .toList();
    }

    @GetMapping("/fieldproof/{id}/photo")
    public ResponseEntity<byte[]> fieldProofPhoto(@PathVariable long id) {
        FieldProofRecord record = fieldProofs.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "FieldProof record not found."));
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(record.getPhotoContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=\"" + record.getPhotoName().replace("\"", "") + "\"")
                .body(record.getPhotoData());
    }

    @DeleteMapping("/fieldproof/{id}")
    @org.springframework.web.bind.annotation.ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteFieldProof(@PathVariable long id) {
        if (!fieldProofs.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "FieldProof record not found.");
        }
        fieldProofs.deleteById(id);
    }

    @GetMapping("/safe-spray")
    public List<SafeSprayRecord> sprayHistory() {
        return sprayRecords.findAllByOrderByAppliedDateDesc();
    }

    @PostMapping("/safe-spray")
    @org.springframework.web.bind.annotation.ResponseStatus(HttpStatus.CREATED)
    public SafeSprayRecord addSpray(@Valid @org.springframework.web.bind.annotation.RequestBody SafeSprayRequest request) {
        if (request.getPlannedHarvestDate() != null && request.getPlannedHarvestDate().isBefore(request.getAppliedDate())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Planned harvest cannot be before the treatment date.");
        }
        SafeSprayRecord record = new SafeSprayRecord();
        record.setCropName(request.getCropName().trim());
        record.setProductName(request.getProductName().trim());
        record.setActiveIngredient(request.getActiveIngredient() == null ? "" : request.getActiveIngredient().trim());
        record.setAppliedDate(request.getAppliedDate());
        record.setPreHarvestIntervalDays(request.getPreHarvestIntervalDays());
        record.setPlannedHarvestDate(request.getPlannedHarvestDate());
        record.setLabelNotes(request.getLabelNotes() == null ? "" : request.getLabelNotes().trim());
        record.setCreatedAt(Instant.now());
        return sprayRecords.save(record);
    }

    @DeleteMapping("/safe-spray/{id}")
    @org.springframework.web.bind.annotation.ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSpray(@PathVariable long id) {
        if (!sprayRecords.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Spray record not found.");
        }
        sprayRecords.deleteById(id);
    }

    private Map<String, Object> fieldProofView(FieldProofRecord record) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("id", record.getId());
        result.put("cropName", record.getCropName());
        result.put("incidentDate", record.getIncidentDate());
        result.put("location", record.getLocation());
        result.put("incidentDetails", record.getIncidentDetails());
        result.put("photoName", record.getPhotoName());
        result.put("photoUrl", "/api/portal/fieldproof/" + record.getId() + "/photo");
        result.put("createdAt", record.getCreatedAt());
        return result;
    }

    private boolean hasJpegSignature(byte[] bytes) {
        return bytes.length >= 3 && (bytes[0] & 0xff) == 0xff && (bytes[1] & 0xff) == 0xd8
                && (bytes[2] & 0xff) == 0xff;
    }

    private boolean hasPngSignature(byte[] bytes) {
        return bytes.length >= 8 && (bytes[0] & 0xff) == 0x89 && bytes[1] == 0x50
                && bytes[2] == 0x4e && bytes[3] == 0x47 && bytes[4] == 0x0d
                && bytes[5] == 0x0a && bytes[6] == 0x1a && bytes[7] == 0x0a;
    }

    private String safeFilename(String filename) {
        String name = filename == null ? "field-photo" : filename.replace("\\", "/");
        name = name.substring(name.lastIndexOf('/') + 1).replaceAll("[^A-Za-z0-9._-]", "_");
        return name.isBlank() ? "field-photo" : name.substring(0, Math.min(name.length(), 160));
    }
}
