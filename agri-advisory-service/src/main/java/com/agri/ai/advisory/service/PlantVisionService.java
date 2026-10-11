package com.agri.ai.advisory.service;

import com.agri.ai.advisory.dto.PlantHealthAssessment;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.util.Base64;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

@Service
public class PlantVisionService {

    private static final Logger log = LoggerFactory.getLogger(PlantVisionService.class);
    private static final Set<String> CONFIDENCE_LEVELS = Set.of("low", "moderate", "high");
    private static final Set<String> SEVERITY_LEVELS = Set.of("low", "moderate", "high", "unknown");

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final String apiKey;
    private final String model;
    private final String apiUrl;

    public PlantVisionService(
            RestTemplate restTemplate,
            ObjectMapper objectMapper,
            @Value("${GOOGLE_API_KEY:}") String apiKey,
            @Value("${plant-vision.model:gemini-3.8-flash}") String model,
            @Value("${plant-vision.api-url:https://generativelanguage.googleapis.com/v1beta}") String apiUrl) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.apiKey = apiKey;
        this.model = model;
        this.apiUrl = apiUrl;
    }

    public PlantHealthAssessment assess(byte[] imageBytes, String mimeType, String cropName, String language) {
        if (!isConfigured()) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                    "Plant photo analysis needs a valid GOOGLE_API_KEY configured for the Advisory service.");
        }

        String prompt = """
                Assess this farmer-submitted plant photo for visible disease or pest symptoms.
                Reply in %s. The crop entered by the farmer is %s; verify visually when possible.
                Do not claim a certain diagnosis from an image. If the photo is unclear or evidence
                is insufficient, say so and set likelyIssue to "Unable to determine from this photo".
                Never recommend pesticide brands, chemical doses, or unsupported treatments.
                Give safe next steps and advise checking with a local agriculture officer or KVK
                before treatment. Confidence is a rough model estimate, not a medical or lab result.
                Return only a JSON object with exactly these string fields:
                cropIdentified, likelyIssue, confidence (low, moderate, or high),
                severity (low, moderate, high, or unknown), observations, recommendations.
                Keep each field concise and understandable to a farmer.
                """.formatted(language, cropName);

        Map<String, Object> requestBody = Map.of(
                "contents", List.of(Map.of(
                        "role", "user",
                        "parts", List.of(
                                Map.of("text", prompt),
                                Map.of("inlineData", Map.of(
                                        "mimeType", mimeType,
                                        "data", Base64.getEncoder().encodeToString(imageBytes)))))),
                "generationConfig", Map.of(
                        "temperature", 0.1,
                        "responseMimeType", "application/json"));
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("x-goog-api-key", apiKey);

        String endpoint = apiUrl.replaceAll("/+$", "") + "/models/" + model + ":generateContent";
        JsonNode response;
        try {
            response = restTemplate.postForObject(endpoint, new HttpEntity<>(requestBody, headers), JsonNode.class);
        } catch (HttpStatusCodeException exception) {
            log.warn("Gemini plant image request failed with HTTP {}", exception.getStatusCode().value());
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,
                    "The image analysis provider rejected the request. Check the Gemini model and API key.");
        } catch (ResourceAccessException exception) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                    "The image analysis provider could not be reached. Please try again later.", exception);
        }

        String content = response == null ? null
                : response.path("candidates").path(0).path("content").path("parts").path(0).path("text").asText(null);
        if (content == null || content.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,
                    "The image analysis provider returned no assessment. Try a clearer plant photo.");
        }

        PlantHealthAssessment assessment;
        try {
            assessment = objectMapper.readValue(content, PlantHealthAssessment.class);
        } catch (Exception exception) {
            log.warn("Gemini returned an invalid plant assessment format.", exception);
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,
                    "The image analysis provider returned an unreadable assessment. Please try again.");
        }
        validateAssessment(assessment);
        assessment.setConfidence(assessment.getConfidence().toLowerCase(Locale.ROOT));
        assessment.setSeverity(assessment.getSeverity().toLowerCase(Locale.ROOT));
        return assessment;
    }

    public boolean isConfigured() {
        return apiKey != null && !apiKey.isBlank()
                && !apiKey.equals("local-dev-key-not-configured")
                && !apiKey.toLowerCase(Locale.ROOT).contains("replace-with");
    }

    private void validateAssessment(PlantHealthAssessment assessment) {
        if (assessment.getCropIdentified() == null || assessment.getCropIdentified().isBlank()
                || assessment.getLikelyIssue() == null || assessment.getLikelyIssue().isBlank()
                || assessment.getObservations() == null || assessment.getObservations().isBlank()
                || assessment.getRecommendations() == null || assessment.getRecommendations().isBlank()
                || assessment.getConfidence() == null
                || !CONFIDENCE_LEVELS.contains(assessment.getConfidence().toLowerCase(Locale.ROOT))
                || assessment.getSeverity() == null
                || !SEVERITY_LEVELS.contains(assessment.getSeverity().toLowerCase(Locale.ROOT))) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,
                    "The image analysis provider returned incomplete assessment details. Please try again.");
        }
    }
}
