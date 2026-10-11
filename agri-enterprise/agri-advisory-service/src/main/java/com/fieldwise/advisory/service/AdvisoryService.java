package com.fieldwise.advisory.service;

import com.fieldwise.advisory.dto.AdvisoryRequest;
import com.fieldwise.advisory.dto.AdvisoryResponse;
import com.fieldwise.advisory.dto.FarmContext;
import com.fieldwise.advisory.dto.PortalLogRequest;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
public class AdvisoryService {

    private final ChatClient chatClient;
    private final CropKnowledgeService cropKnowledgeService;
    private final MockWeatherService mockWeatherService;
    private final RestTemplate restTemplate;
    private final String farmerWorkspaceUrl;
    private final String portalServiceUrl;

    public AdvisoryService(
            ChatClient.Builder chatClientBuilder,
            CropKnowledgeService cropKnowledgeService,
            MockWeatherService mockWeatherService,
            RestTemplate restTemplate,
            @Value("${services.farmer-workspace-url}") String farmerWorkspaceUrl,
            @Value("${services.portal-url}") String portalServiceUrl) {
        this.chatClient = chatClientBuilder.build();
        this.cropKnowledgeService = cropKnowledgeService;
        this.mockWeatherService = mockWeatherService;
        this.restTemplate = restTemplate;
        this.farmerWorkspaceUrl = farmerWorkspaceUrl;
        this.portalServiceUrl = portalServiceUrl;
    }

    public AdvisoryResponse process(AdvisoryRequest request) {
        String category = classify(request.getQuery());
        List<String> cropEvidence = cropKnowledgeService.retrieve(request.getQuery());
        String weatherEvidence = mockWeatherService.forecast(request.getLocation());
        FarmContext farmContext = getFarmContext(request.getLocation());

        List<String> sources = new ArrayList<>(cropEvidence);
        sources.add("[WEATHER-MOCK-01] " + request.getLocation() + ": " + weatherEvidence);
        sources.addAll(farmContext.getReferences());

        String systemPrompt = """
                You are an agricultural information assistant. Reply entirely in the requested language: %s.
                Use simple, practical wording. Ground factual claims only in the retrieved evidence and sensor/
                vision context below. Cite supporting items using their exact IDs in square brackets, such as
                [CROP-RICE-01] or [WEATHER-MOCK-01]. Never invent a diagnosis, sensor reading, forecast,
                pesticide dose, market price, or scheme eligibility. If evidence is insufficient, say so and
                recommend checking with a local agriculture officer. The weather values are mock demo values,
                not live forecasts. Vision detections are model metadata, not a confirmed diagnosis.

                Query category: %s
                Location: %s
                Retrieved crop knowledge:
                %s

                Weather context:
                [WEATHER-MOCK-01] %s

                Farmer's registered farm and crop plan:
                %s
                """.formatted(
                request.getLanguage(), category, request.getLocation(),
                String.join("\n", cropEvidence), weatherEvidence,
                farmContext.getFarmSummary() + "\n"
                        + farmContext.getCropPlanSummary() + "\n"
                        + farmContext.getUpcomingTasks());

        String answer = chatClient.prompt()
                .system(systemPrompt)
                .user(request.getQuery())
                .call()
                .content();
        if (answer == null || answer.isBlank()) {
            throw new IllegalStateException("The configured AI provider returned an empty answer.");
        }

        AdvisoryResponse response = new AdvisoryResponse(
                request.getLanguage(), category, answer.trim(), sources);
        restTemplate.postForLocation(
                portalServiceUrl + "/api/portal/log",
                new PortalLogRequest(request.getQuery(), response.getDetectedLanguage(),
                        response.getAnswer(), response.getSources()));
        return response;
    }

    public String classify(String query) {
        String text = query.toLowerCase(Locale.ROOT);
        if (containsAny(text, "weather", "rain", "rainfall", "temperature", "மழை")) {
            return "weather";
        }
        if (containsAny(text, "pest", "insect", "aphid", "bollworm", "பூச்சி", "कीट")) {
            return "pest management";
        }
        if (containsAny(text, "disease", "blight", "fungus", "spot", "நோய்", "रोग")) {
            return "disease management";
        }
        if (containsAny(text, "market", "price", "mandi", "भाव", "சந்தை")) {
            return "market information";
        }
        if (containsAny(text, "scheme", "subsidy", "pm-kisan", "योजना", "திட்டம்")) {
            return "government schemes";
        }
        if (containsAny(text, "crop", "rice", "paddy", "cotton", "cultivate", "நெல்", "धान")) {
            return "crop and cultivation";
        }
        return "general agriculture";
    }

    private FarmContext getFarmContext(String location) {
        try {
            FarmContext context = restTemplate.getForObject(
                    farmerWorkspaceUrl + "/api/farmers/context?location={location}",
                    FarmContext.class,
                    location);
            if (context != null) {
                return context;
            }
        } catch (RestClientException exception) {
            FarmContext unavailable = unavailableFarm(location);
            unavailable.setReferences(List.of("[FARM-PROFILE-UNAVAILABLE] " + exception.getMessage()));
            return unavailable;
        }
        return unavailableFarm(location);
    }

    private FarmContext unavailableFarm(String location) {
        FarmContext context = new FarmContext();
        context.setLocation(location);
        context.setFarmSummary("No saved farmer profile for this district.");
        context.setCropPlanSummary("No saved crop plans for this district.");
        context.setUpcomingTasks("No registered field tasks for this district.");
        return context;
    }

    private boolean containsAny(String text, String... terms) {
        for (String term : terms) {
            if (text.contains(term)) {
                return true;
            }
        }
        return false;
    }
}
