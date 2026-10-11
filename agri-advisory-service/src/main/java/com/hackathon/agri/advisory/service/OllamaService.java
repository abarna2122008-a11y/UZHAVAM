package com.hackathon.agri.advisory.service;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class OllamaService {

    private final RestTemplate restTemplate;
    private final String ollamaUrl;
    private final String model;

    public OllamaService(
            RestTemplate restTemplate,
            @Value("${ollama.base-url}") String ollamaUrl,
            @Value("${ollama.model}") String model) {
        this.restTemplate = restTemplate;
        this.ollamaUrl = ollamaUrl;
        this.model = model;
    }

    public String generate(String question, String language, String category, String knowledge, String weather) {
        String prompt = """
                You are a careful agricultural information assistant. Answer in %s, using simple language.
                Use only the retrieved notes and weather context below. Do not invent measurements, prices,
                scheme eligibility, diagnoses, or pesticide doses. If the evidence is insufficient, say what
                information is missing and suggest checking with a local agriculture officer. Keep the answer
                practical and concise, and distinguish measured data from general guidance.

                Query category: %s
                Farmer's question: %s

                Retrieved agricultural notes:
                %s

                District weather context:
                %s
                """.formatted(language, category, question, knowledge, weather);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        Map<String, Object> request = Map.of(
                "model", model,
                "prompt", prompt,
                "stream", false,
                "options", Map.of("temperature", 0.2));
        JsonNode response;
        try {
            response = restTemplate.postForObject(
                    ollamaUrl + "/api/generate",
                    new HttpEntity<>(request, headers),
                    JsonNode.class);
        } catch (RestClientException exception) {
            throw new RestClientException(
                    "Could not reach Ollama at " + ollamaUrl + " using model " + model, exception);
        }

        if (response == null || response.path("response").asText().isBlank()) {
            throw new RestClientException("Ollama returned an empty response.");
        }
        return response.path("response").asText().trim();
    }
}
