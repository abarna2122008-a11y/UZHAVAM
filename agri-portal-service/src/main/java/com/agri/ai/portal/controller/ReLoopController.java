package com.agri.ai.portal.controller;

import com.agri.ai.portal.dto.ReLoopListingRequest;
import com.agri.ai.portal.dto.ReLoopCollectionRequest;
import com.agri.ai.portal.dto.ReLoopReservationRequest;
import com.agri.ai.portal.model.ReLoopListing;
import com.agri.ai.portal.repository.ReLoopListingRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

@Validated
@RestController
@RequestMapping("/api/portal/reloop")
public class ReLoopController {
    private static final Set<String> MATERIAL_TYPES = Set.of(
            "Crop residue", "Reusable item", "Compost material", "Processing by-product");
    private static final Set<String> UNITS = Set.of("kg", "quintal", "tonne", "piece", "bundle", "bag");
    private final ReLoopListingRepository listings;

    public ReLoopController(ReLoopListingRepository listings) {
        this.listings = listings;
    }

    @GetMapping
    public List<Map<String, Object>> browse(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String district,
            @RequestParam(required = false) String type) {
        String query = search == null ? "" : search.trim().toLowerCase(Locale.ROOT);
        String area = district == null ? "" : district.trim().toLowerCase(Locale.ROOT);
        String category = type == null ? "" : type.trim();
        return listings.findAllByOrderByCreatedAtDesc().stream()
                .filter(item -> "Available".equals(item.getStatus()) || "Reserved".equals(item.getStatus()))
                .filter(item -> query.isEmpty() || (item.getMaterialName() + " " + item.getDetails())
                        .toLowerCase(Locale.ROOT).contains(query))
                .filter(item -> area.isEmpty() || item.getDistrict().toLowerCase(Locale.ROOT).contains(area))
                .filter(item -> category.isEmpty() || item.getMaterialType().equalsIgnoreCase(category))
                .map(this::listingView)
                .toList();
    }

    @GetMapping("/impact")
    public Map<String, Object> impact() {
        List<ReLoopListing> collected = listings.findAllByOrderByCreatedAtDesc().stream()
                .filter(item -> "Collected".equals(item.getStatus()) && item.getCollectedQuantity() != null)
                .toList();
        Map<String, BigDecimal> quantitiesByUnit = new LinkedHashMap<>();
        collected.forEach(item -> quantitiesByUnit.merge(item.getUnit(), item.getCollectedQuantity(), BigDecimal::add));
        return Map.of(
                "completedPickups", collected.size(),
                "quantitiesByUnit", quantitiesByUnit,
                "note", "Totals use user-entered pickup records and are not independently verified.");
    }

    @GetMapping("/catalogue")
    public List<Map<String, String>> reuseCatalogue() {
        return List.of(
                Map.of("material", "Paddy straw", "uses", "Bedding or feed only where suitable, mushroom substrate, compost, or approved biomass use.", "caution", "Check mould, pesticide history, moisture, and local feed safety before reuse."),
                Map.of("material", "Coconut husk / coir", "uses", "Coir products, nursery media, mulch, or compost after suitable processing.", "caution", "Salinity and processing needs vary; use a crop-appropriate, well-processed product."),
                Map.of("material", "Clean reusable crates and pots", "uses", "Clean, inspect, and reuse for produce handling or nursery work.", "caution", "Do not reuse damaged containers for food handling."),
                Map.of("material", "Healthy plant trimmings", "uses", "Compost or mulch when the material is disease-free and suitable.", "caution", "Do not spread diseased or invasive plant material; follow local disposal rules."),
                Map.of("material", "Oilseed / pulse processing by-product", "uses", "Potential compost, industrial, or feed use depending on material and processing.", "caution", "Feed suitability depends on species, processing, contaminants, and expert guidance."));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, Object> create(@Valid @RequestBody ReLoopListingRequest request) {
        if (!MATERIAL_TYPES.contains(request.getMaterialType())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Choose a listed material type.");
        }
        if (!UNITS.contains(request.getUnit())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Choose a supported quantity unit.");
        }
        ReLoopListing item = new ReLoopListing();
        item.setMaterialName(request.getMaterialName().trim());
        item.setMaterialType(request.getMaterialType());
        item.setQuantity(request.getQuantity());
        item.setUnit(request.getUnit());
        item.setAskingPrice(request.getAskingPrice());
        item.setDistrict(request.getDistrict().trim());
        item.setSellerName(request.getSellerName().trim());
        item.setContactPhone(request.getContactPhone() == null ? "" : request.getContactPhone().trim());
        item.setDetails(request.getDetails() == null ? "" : request.getDetails().trim());
        item.setAvailableFrom(request.getAvailableFrom());
        item.setStatus("Available");
        item.setCreatedAt(Instant.now());
        return listingView(listings.save(item));
    }

    @PatchMapping("/{id}/status")
    public Map<String, Object> updateStatus(@PathVariable long id, @RequestParam String status) {
        ReLoopListing item = listings.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ReLoop listing not found."));
        String next = status.trim();
        boolean allowed = ("Available".equals(item.getStatus()) && "Reserved".equals(next))
                || ("Reserved".equals(item.getStatus()) && "Available".equals(next));
        if (!allowed) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "A listing can move from Available to Reserved, or from Reserved back to Available. Use pickup confirmation to record collection.");
        }
        item.setStatus(next);
        if ("Available".equals(next)) {
            item.setReservedBy("");
            item.setReservationPhone("");
            item.setPickupDate(null);
        }
        return listingView(listings.save(item));
    }

    @PostMapping("/{id}/collection-confirmation")
    public Map<String, Object> confirmCollection(
            @PathVariable long id,
            @Valid @RequestBody ReLoopCollectionRequest request) {
        ReLoopListing item = listings.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ReLoop listing not found."));
        if (!"Reserved".equals(item.getStatus())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Only a reserved listing can be confirmed as collected.");
        }
        if (request.getCollectedQuantity().compareTo(item.getQuantity()) > 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Collected quantity cannot exceed the listed quantity.");
        }
        item.setCollectedQuantity(request.getCollectedQuantity());
        item.setSellerPickupConfirmation(request.getSellerConfirmation().trim());
        item.setBuyerPickupConfirmation(request.getBuyerConfirmation().trim());
        item.setCollectedAt(Instant.now());
        item.setStatus("Collected");
        return listingView(listings.save(item));
    }

    @PostMapping("/{id}/reserve")
    public Map<String, Object> reserve(
            @PathVariable long id,
            @Valid @RequestBody ReLoopReservationRequest request) {
        ReLoopListing item = listings.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ReLoop listing not found."));
        if (!"Available".equals(item.getStatus())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "This listing is no longer available.");
        }
        if (request.getPickupDate() != null && item.getAvailableFrom() != null
                && request.getPickupDate().isBefore(item.getAvailableFrom())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Pickup cannot be before the listing is available.");
        }
        item.setStatus("Reserved");
        item.setReservedBy(request.getReservedBy().trim());
        item.setReservationPhone(request.getContactPhone().trim());
        item.setPickupDate(request.getPickupDate());
        return listingView(listings.save(item));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable long id) {
        if (!listings.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "ReLoop listing not found.");
        }
        listings.deleteById(id);
    }

    private Map<String, Object> listingView(ReLoopListing item) {
        Map<String, Object> result = new java.util.LinkedHashMap<>();
        result.put("id", item.getId());
        result.put("materialName", item.getMaterialName());
        result.put("materialType", item.getMaterialType());
        result.put("quantity", item.getQuantity());
        result.put("unit", item.getUnit());
        result.put("askingPrice", item.getAskingPrice());
        result.put("district", item.getDistrict());
        result.put("sellerName", item.getSellerName());
        result.put("contactPhone", item.getContactPhone());
        result.put("details", item.getDetails());
        result.put("availableFrom", item.getAvailableFrom());
        result.put("reservedBy", item.getReservedBy());
        result.put("reservationPhone", item.getReservationPhone());
        result.put("pickupDate", item.getPickupDate());
        result.put("collectedQuantity", item.getCollectedQuantity());
        result.put("sellerPickupConfirmation", item.getSellerPickupConfirmation());
        result.put("buyerPickupConfirmation", item.getBuyerPickupConfirmation());
        result.put("collectedAt", item.getCollectedAt());
        result.put("status", item.getStatus());
        result.put("createdAt", item.getCreatedAt());
        result.put("reuseSuggestions", reuseSuggestions(item.getMaterialName()));
        return result;
    }

    private List<String> reuseSuggestions(String material) {
        String name = material.toLowerCase(Locale.ROOT);
        if (name.contains("straw") || name.contains("paddy") || name.contains("rice")) {
            return List.of("Compost or suitable mushroom substrate", "Bedding or feed only after checking suitability", "Approved biomass use");
        }
        if (name.contains("coconut") || name.contains("coir") || name.contains("husk")) {
            return List.of("Coir processing", "Nursery media or mulch after suitable processing", "Compost");
        }
        if (name.contains("crate") || name.contains("pot") || name.contains("container")) {
            return List.of("Clean, inspect, and reuse for produce handling", "Reuse as nursery containers if food-safe and undamaged");
        }
        if (name.contains("leaf") || name.contains("trimming") || name.contains("plant residue")) {
            return List.of("Compost or mulch only if disease-free and locally suitable");
        }
        return List.of("Check the reviewed reuse catalogue and consult a local extension officer before feed, soil, or food-contact use.");
    }
}
