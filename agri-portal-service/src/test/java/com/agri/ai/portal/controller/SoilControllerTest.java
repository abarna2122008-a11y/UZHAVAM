package com.agri.ai.portal.controller;

import com.agri.ai.portal.dto.SoilResultRequest;
import com.agri.ai.portal.model.SoilResult;
import com.agri.ai.portal.repository.SoilResultRepository;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SoilControllerTest {

    @Test
    void storesMeasuredPhWithTransparentScreeningInterpretation() {
        SoilResultRepository repository = mock(SoilResultRepository.class);
        when(repository.save(any(SoilResult.class))).thenAnswer(invocation -> invocation.getArgument(0));
        SoilController controller = new SoilController(repository);
        SoilResultRequest request = new SoilResultRequest();
        request.setTestType("pH");
        request.setValue(new BigDecimal("8.3"));
        request.setUnit("pH");
        request.setMethod("Home screening");
        request.setFieldName("North field");
        request.setCropName("Rice");
        request.setTestedOn(LocalDate.now());
        request.setNotes("Commercial strip");

        SoilResult saved = controller.save(request);

        assertEquals("Home screening", saved.getMethod());
        assertEquals("Home screening", saved.getAccuracyClass());
        assertTrue(saved.getInterpretation().startsWith("Alkaline-range reading."));
        assertTrue(saved.getInterpretation().contains("confirm with a soil laboratory"));
    }

    @Test
    void soilCatalogueDoesNotOfferPotassiumHomeTest() {
        SoilController controller = new SoilController(mock(SoilResultRepository.class));

        assertTrue(controller.catalogue().stream().noneMatch(item -> item.get("name").toString().toLowerCase().contains("potassium")));
        assertTrue(controller.labAndShopDirectories().stream().anyMatch(item -> item.get("name").equals("Soil Health Card programme")));
    }

    @Test
    void acceptsNutrientValuesOnlyAsLabReportedReadings() {
        SoilResultRepository repository = mock(SoilResultRepository.class);
        when(repository.save(any(SoilResult.class))).thenAnswer(invocation -> invocation.getArgument(0));
        SoilController controller = new SoilController(repository);
        SoilResultRequest request = new SoilResultRequest();
        request.setTestType("Lab potassium");
        request.setValue(new BigDecimal("180"));
        request.setUnit("mg/kg");
        request.setMethod("Lab");
        request.setFieldName("North field");
        request.setTestedOn(LocalDate.now());

        SoilResult saved = controller.save(request);

        assertEquals("Lab potassium", saved.getTestType());
        assertEquals("180", saved.getValue().toPlainString());
        assertEquals("mg/kg", saved.getUnit());
        assertEquals("Laboratory result", saved.getAccuracyClass());
    }

    @Test
    void rejectsLabNutrientsEnteredAsHomeScreening() {
        SoilController controller = new SoilController(mock(SoilResultRepository.class));
        SoilResultRequest request = new SoilResultRequest();
        request.setTestType("Lab potassium");
        request.setValue(new BigDecimal("180"));
        request.setUnit("kg/ha");
        request.setMethod("Home screening");
        request.setFieldName("North field");
        request.setTestedOn(LocalDate.now());

        org.junit.jupiter.api.Assertions.assertThrows(ResponseStatusException.class, () -> controller.save(request));
    }
}
