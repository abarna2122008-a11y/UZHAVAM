package com.agri.ai.portal.controller;

import com.agri.ai.portal.model.FieldProofRecord;
import com.agri.ai.portal.repository.FieldProofRepository;
import com.agri.ai.portal.repository.SafeSprayRepository;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CropSafetyControllerTest {

    @Test
    void savesAndReturnsValidatedEvidencePhotoWithoutLeakingTheImageInListView() throws Exception {
        FieldProofRepository proofs = mock(FieldProofRepository.class);
        SafeSprayRepository sprays = mock(SafeSprayRepository.class);
        AtomicReference<FieldProofRecord> savedRecord = new AtomicReference<>();
        when(proofs.save(any(FieldProofRecord.class))).thenAnswer(invocation -> {
            FieldProofRecord record = invocation.getArgument(0);
            record.setId(12L);
            savedRecord.set(record);
            return record;
        });
        when(proofs.findById(12L)).thenAnswer(invocation -> Optional.of(savedRecord.get()));
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        ImageIO.write(new BufferedImage(2, 2, BufferedImage.TYPE_INT_RGB), "png", output);
        byte[] imageBytes = output.toByteArray();
        MockMultipartFile photo = new MockMultipartFile("photo", "..\\field-proof.png", "image/png", imageBytes);
        CropSafetyController controller = new CropSafetyController(proofs, sprays);

        var response = controller.addFieldProof(
                "Rice", LocalDate.of(2026, 10, 10), "Thanjavur", "Water damage observed", photo);
        var downloaded = controller.fieldProofPhoto(12L);

        assertEquals(12L, response.get("id"));
        assertEquals("field-proof.png", response.get("photoName"));
        assertEquals("/api/portal/fieldproof/12/photo", response.get("photoUrl"));
        assertArrayEquals(imageBytes, downloaded.getBody());
    }
}
