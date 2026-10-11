package com.agri.ai.advisory.controller;

import com.agri.ai.advisory.dto.AdvisoryRequest;
import com.agri.ai.advisory.service.LocalAdvisoryFallback;
import com.agri.ai.advisory.service.PlantVisionService;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AdvisoryControllerTest {

    @Test
    void returnsLocalAnswerWhenGeminiHasNoConfiguredKey() {
        ChatClient.Builder chatBuilder = mock(ChatClient.Builder.class);
        when(chatBuilder.build()).thenReturn(mock(ChatClient.class));
        PlantVisionService visionService = mock(PlantVisionService.class);
        when(visionService.isConfigured()).thenReturn(false);
        AdvisoryController controller = new AdvisoryController(
                chatBuilder, visionService, new LocalAdvisoryFallback(),
                mock(RestTemplate.class), "http://localhost:8081");

        var response = controller.process(new AdvisoryRequest(
                "How do I maintain coconut trees?", "English", ""));

        assertTrue(response.getResponse().contains("coconut-care guidance"));
        assertTrue(response.getSources().stream().anyMatch(source -> source.contains("Gemini is not configured")));
    }
}
