package com.agri.ai.advisory.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class LocalAdvisoryFallbackTest {

    private final LocalAdvisoryFallback fallback = new LocalAdvisoryFallback();

    @Test
    void givesCoconutCareChecklistWhenGeminiIsNotConfigured() {
        String answer = fallback.answer("How do I maintain coconut trees?", "English");

        assertTrue(answer.contains("coconut-care guidance"));
        assertTrue(answer.contains("without waterlogging"));
        assertTrue(answer.contains("soil test"));
    }

    @Test
    void answersCoconutQuestionInSelectedLanguage() {
        String answer = fallback.answer("தேங்காய் மரம் எப்படி பராமரிப்பது?", "Tamil");

        assertTrue(answer.contains("Gemini AI அமைக்கப்படவில்லை"));
        assertTrue(answer.contains("தென்னை"));
    }

    @Test
    void givesGeneralGuidanceForOtherQuestions() {
        String answer = fallback.answer("How do I grow beans?", "Hindi");

        assertTrue(answer.contains("Gemini AI अभी कॉन्फ़िगर नहीं है"));
        assertTrue(answer.contains("मिट्टी"));
    }
}
