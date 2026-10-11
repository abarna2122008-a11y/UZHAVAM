package com.hackathon.agri.advisory.service;

import com.hackathon.agri.advisory.dto.AdvisoryRequest;
import com.hackathon.agri.advisory.dto.AdvisoryResponse;
import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AdvisoryServiceTest {

    private final WeatherService weatherService = mock(WeatherService.class);
    private final OllamaService ollamaService = mock(OllamaService.class);
    private final AdvisoryService advisoryService = new AdvisoryService(weatherService, ollamaService);

    @Test
    void detectsAllSupportedScriptsAndHindiMarathiKeywords() {
        assertEquals("English", advisoryService.detectLanguage("How is my crop?"));
        assertEquals("Tamil", advisoryService.detectLanguage("என் பயிர்"));
        assertEquals("Telugu", advisoryService.detectLanguage("నా పంట"));
        assertEquals("Kannada", advisoryService.detectLanguage("ನನ್ನ ಬೆಳೆ"));
        assertEquals("Malayalam", advisoryService.detectLanguage("എന്റെ വിള"));
        assertEquals("Hindi", advisoryService.detectLanguage("मेरी फसल"));
        assertEquals("Marathi", advisoryService.detectLanguage("माझ्या शेतात"));
    }

    @Test
    void classifiesCommonAgriculturalQuestionsAndRetrievesGroundedNotes() {
        assertEquals("market information", advisoryService.classifyQuery("What is today's mandi price?"));
        assertEquals("government schemes", advisoryService.classifyQuery("Am I eligible for a subsidy scheme?"));
        assertEquals("weather", advisoryService.classifyQuery("Will it rain in my district?"));
        assertEquals("disease management", advisoryService.classifyQuery("How do I control leaf blight disease?"));
        assertEquals("pest management", advisoryService.classifyQuery("How do I manage aphid pests?"));
        assertTrue(advisoryService.retrieveKnowledge("How should I grow rice?", "crop selection")
                .contains("Rice:"));
        assertTrue(advisoryService.retrieveKnowledge("What is the market price?", "market information")
                .contains("e-NAM"));
    }

    @Test
    void usesSelectedReplyLanguageForGeneration() {
        when(weatherService.getForecast("Pune"))
                .thenReturn(new WeatherService.WeatherContext("Forecast unavailable.", "Open-Meteo unavailable."));
        when(ollamaService.generate(anyString(), eq("Tamil"), anyString(), anyString(), anyString()))
                .thenReturn("தமிழில் ஆலோசனை");

        AdvisoryResponse response = advisoryService.process(
                new AdvisoryRequest("How should I prepare my soil?", "Pune", "Tamil"));

        assertEquals("Tamil", response.getDetectedLanguage());
        assertEquals("தமிழில் ஆலோசனை", response.getAnswer());
        verify(ollamaService).generate(anyString(), eq("Tamil"), anyString(), anyString(), anyString());
    }
}
