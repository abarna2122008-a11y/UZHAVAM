package com.agri.ai.advisory.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PlantVisionServiceTest {

    @Test
    void reportsWhetherImageAnalysisHasAConfiguredKeyWithoutExposingIt() {
        PlantVisionService configured = new PlantVisionService(
                new RestTemplate(), new ObjectMapper(), "private-key", "gemini-2.0-flash", "https://example.test");
        PlantVisionService missing = new PlantVisionService(
                new RestTemplate(), new ObjectMapper(), "", "gemini-2.0-flash", "https://example.test");
        PlantVisionService placeholder = new PlantVisionService(
                new RestTemplate(), new ObjectMapper(), "replace-with-key", "gemini-2.0-flash", "https://example.test");

        assertTrue(configured.isConfigured());
        assertFalse(missing.isConfigured());
        assertFalse(placeholder.isConfigured());
    }
}
