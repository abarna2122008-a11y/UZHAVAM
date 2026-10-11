package com.agri.ai.advisory.controller;

import com.agri.ai.advisory.dto.AdvisoryRequest;
import com.agri.ai.advisory.dto.AdvisoryResponse;
import com.agri.ai.advisory.dto.PlantHealthAssessment;
import com.agri.ai.advisory.dto.PlantDiagnosisReport;
import com.agri.ai.advisory.service.LocalAdvisoryFallback;
import com.agri.ai.advisory.service.PlantVisionService;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.ai.retry.NonTransientAiException;
import org.springframework.ai.retry.TransientAiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Path;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.util.Iterator;

@Validated
@RestController
@RequestMapping("/api/advisory")
public class AdvisoryController {

    private static final Logger log = LoggerFactory.getLogger(AdvisoryController.class);
    private static final List<String> AGRICULTURE_NOTES = List.of(
            "[CROP-RICE-01] Rice: use a locally recommended variety and sowing window. Base fertilizer decisions on soil testing and local extension guidance.",
            "[CROP-IPM-01] Pest management: identify the pest and assess crop damage before treatment. Ask a local agriculture officer about integrated pest management.",
            "[CROP-SOIL-01] Crop choice depends on soil, season, irrigation access, and nearby market demand; location by itself is not enough evidence.",
            "[WATER-IRRIGATION-01] Irrigation: check soil moisture at root depth and the crop's growth stage before watering. Avoid prolonged waterlogging; local schedules depend on soil and crop.",
            "[SOIL-HEALTH-01] Soil health: a soil test is the reliable way to learn pH and nutrient status. Follow the local soil laboratory or agriculture department recommendation.",
            "[CROP-DISEASE-01] Crop disease: inspect affected and healthy plants, note how symptoms spread, and get the crop and disease identified before choosing a treatment.",
            "[POST-HARVEST-01] Post-harvest: dry and store produce according to the crop's safe moisture and storage requirements; those limits vary by crop and local conditions.",
            "[SCHEME-PMKISAN-01] PM-KISAN: official portal has current eligibility and enrollment status; eligibility is decided by the scheme authority.",
            "[MARKET-ENAM-01] e-NAM lists market arrivals and prices; check the chosen mandi and date before deciding where to sell.",
            "[GENERAL-EXTENSION-01] General crop advice: identify the crop, variety, growth stage, soil, water access, and symptoms before making a field recommendation. For urgent disease or pesticide decisions, confirm locally with an agriculture extension officer or KVK.");
    private static final Set<String> SUPPORTED_LANGUAGES = Set.of(
            "English", "Tamil", "Hindi", "Malayalam", "Kannada", "Telugu", "Marathi");
    private static final int MAX_IMAGE_BYTES = 8 * 1024 * 1024;
    private static final long MAX_IMAGE_PIXELS = 20_000_000L;

    private final ChatClient chatClient;
    private final PlantVisionService plantVisionService;
    private final LocalAdvisoryFallback localAdvisoryFallback;
    private final RestTemplate restTemplate;
    private final String portalServiceUrl;

    public AdvisoryController(
            ChatClient.Builder chatClientBuilder,
            PlantVisionService plantVisionService,
            LocalAdvisoryFallback localAdvisoryFallback,
            RestTemplate restTemplate,
            @Value("${services.portal-url:http://localhost:8081}") String portalServiceUrl) {
        this.chatClient = chatClientBuilder.build();
        this.plantVisionService = plantVisionService;
        this.localAdvisoryFallback = localAdvisoryFallback;
        this.restTemplate = restTemplate;
        this.portalServiceUrl = portalServiceUrl;
    }

    @GetMapping("/vision/status")
    public Map<String, Boolean> plantVisionStatus() {
        return Map.of("configured", plantVisionService.isConfigured());
    }

    @PostMapping(value = "/diagnose", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public JsonNode diagnosePlant(
            @RequestParam("image") MultipartFile image,
            @RequestParam("cropName") @NotBlank String cropName,
            @RequestParam("language") @NotBlank String language,
            @RequestParam(value = "district", required = false) String district) {
        if (!SUPPORTED_LANGUAGES.contains(language.trim())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Choose one of the supported languages.");
        }
        if (image.isEmpty() || image.getSize() > MAX_IMAGE_BYTES) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Choose a plant photo smaller than 8 MB.");
        }
        String mimeType = image.getContentType();
        if (!MediaType.IMAGE_JPEG_VALUE.equals(mimeType) && !MediaType.IMAGE_PNG_VALUE.equals(mimeType)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Upload a JPEG or PNG plant photo.");
        }

        byte[] photo;
        try {
            photo = image.getBytes();
            if (!isValidPlantImage(photo, mimeType)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "The photo is invalid or too large to process. Try a smaller, clear image.");
            }
        } catch (IOException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The uploaded photo could not be read.");
        }

        PlantHealthAssessment assessment = plantVisionService.assess(photo, mimeType, cropName.trim(), language.trim());
        List<String> sources = List.of(
                "[AI-VISION-GEMINI] Visual assessment generated from the submitted plant photo; confidence is an estimate.",
                "[LOCAL-EXTENSION-01] Confirm uncertain disease or pest identification with a local agriculture officer or KVK before treatment.");
        PlantDiagnosisReport report = new PlantDiagnosisReport();
        report.setCropName(cropName.trim());
        report.setDistrict(district == null || district.isBlank() ? "Not provided" : district.trim());
        report.setLanguage(language.trim());
        report.setCropIdentified(assessment.getCropIdentified());
        report.setLikelyIssue(assessment.getLikelyIssue());
        report.setConfidence(assessment.getConfidence());
        report.setSeverity(assessment.getSeverity());
        report.setObservations(assessment.getObservations());
        report.setRecommendations(assessment.getRecommendations());
        report.setImageName(safeImageName(image.getOriginalFilename()));
        report.setSourceReferences(sources);

        try {
            return restTemplate.postForObject(
                    portalServiceUrl + "/api/portal/diagnoses",
                    report,
                    JsonNode.class);
        } catch (RestClientException exception) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                    "The diagnosis was generated but could not be saved to the Portal. Please try again.", exception);
        }
    }

    private boolean isValidPlantImage(byte[] photo, String mimeType) throws IOException {
        try (ImageInputStream input = ImageIO.createImageInputStream(new ByteArrayInputStream(photo))) {
            if (input == null) return false;
            Iterator<ImageReader> readers = ImageIO.getImageReaders(input);
            if (!readers.hasNext()) return false;
            ImageReader reader = readers.next();
            try {
                reader.setInput(input, true, true);
                String format = reader.getFormatName().toLowerCase(Locale.ROOT);
                if (MediaType.IMAGE_PNG_VALUE.equals(mimeType) && !"png".equals(format)) return false;
                if (MediaType.IMAGE_JPEG_VALUE.equals(mimeType)
                        && !("jpeg".equals(format) || "jpg".equals(format))) return false;
                long pixels = (long) reader.getWidth(0) * reader.getHeight(0);
                return pixels > 0 && pixels <= MAX_IMAGE_PIXELS && reader.read(0) != null;
            } finally {
                reader.dispose();
            }
        }
    }

    private String safeImageName(String originalName) {
        if (originalName == null || originalName.isBlank()) return "plant-photo";
        String filename = Path.of(originalName.replace('\\', '/')).getFileName().toString();
        String safeName = filename.replaceAll("[^A-Za-z0-9._-]", "_");
        return safeName.length() > 120 ? safeName.substring(safeName.length() - 120) : safeName;
    }

    @PostMapping("/process")
    public AdvisoryResponse process(@Valid @RequestBody AdvisoryRequest request) {
        String language = request.getLanguage().trim();
        if (!SUPPORTED_LANGUAGES.contains(language)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Choose one of the supported languages.");
        }
        String location = request.getLocation() == null || request.getLocation().isBlank()
                ? "not provided" : request.getLocation().trim();
        String category = classify(request.getQuery());
        String weather = "No district was provided, so no current local weather is available.";
        if (!"not provided".equals(location)) {
            try {
                Map<String, Object> currentWeather = currentWeather(location);
                weather = currentWeather.get("summary") + ", "
                        + currentWeather.get("temperatureC") + " C, rain chance "
                        + currentWeather.get("rainChancePercent") + "%, wind "
                        + currentWeather.get("windKmh") + " km/h.";
            } catch (ResponseStatusException exception) {
                log.warn("Current weather was not added to the advisory context for district {}: {}",
                        location, exception.getReason());
                weather = exception.getStatusCode() == HttpStatus.NOT_FOUND
                        ? "No matching district was found; no local weather is available."
                        : "Live weather is temporarily unavailable; do not infer current conditions.";
            }
        }
        String farmRecords = getFarmRecords(location);
        List<String> sources = sourcesFor(category);
        sources.add("[WEATHER-OPEN-METEO-01] " + weather);
        sources.add("[FARM-RECORD-01] Farmer-entered profile and crop-plan records when saved.");

        String systemPrompt = """
                You are Uzhavam, a practical agriculture assistant for Indian farmers.
                Reply entirely in %s using short, clear, respectful language suitable for a farmer.
                Answer the user's actual question directly, including questions about cultivation,
                soil, irrigation, seeds, crop stages, pests, disease symptoms, harvest, storage,
                livestock, weather, markets, and government schemes. Do not give a generic refusal
                just because a topic is not in the small evidence list.
                Use retrieved evidence and the saved farm context for local or user-specific facts.
                You may add cautious, widely accepted general agricultural guidance when evidence is
                limited, but label it as general guidance and do not present it as a local fact.
                Never invent weather, market prices, yields, soil test results, disease diagnoses,
                pesticide products or doses, scheme eligibility, or government decisions.
                Cite evidence IDs only when the cited note actually supports the claim. If a specific
                answer needs information we do not have, say which information is missing and ask one
                useful follow-up question or suggest a local Krishi Vigyan Kendra/agriculture officer.
                Weather data, when supplied, is a current forecast estimate and not an official warning.
                If no weather data is supplied, say current local conditions are unavailable.
                Official scheme and market links are lookup pages, not live eligibility or price feeds.

                Category: %s
                Farmer location: %s
                Retrieved agricultural notes (small prototype knowledge set):
                %s

                District weather:
                [WEATHER-OPEN-METEO-01] %s

                Farmer's saved records:
                %s
                """.formatted(language, category, location, String.join("\n", sourcesFor(category)), weather, farmRecords);

        String answer;
        if (!plantVisionService.isConfigured()) {
            answer = localAdvisoryFallback.answer(request.getQuery(), language);
            sources.clear();
            sources.add("[LOCAL-GENERAL-GUIDANCE] Gemini is not configured; this response is a built-in definition or checklist, not an AI-generated crop-specific recommendation.");
        } else {
            try {
                answer = chatClient.prompt()
                        .system(systemPrompt)
                        .user(request.getQuery())
                        .call()
                        .content();
                if (answer == null || answer.isBlank()) {
                    throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Gemini returned an empty answer.");
                }
            } catch (NonTransientAiException exception) {
                log.warn("Gemini rejected the advisory request: {}", exception.getMessage());
                throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,
                        "Gemini rejected the request. Check that GOOGLE_API_KEY is valid and GEMINI_MODEL is available.", exception);
            } catch (TransientAiException exception) {
                throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                        "Gemini is temporarily unavailable. Please try again shortly.", exception);
            }
        }
        AdvisoryResponse response = new AdvisoryResponse(
                request.getQuery(), language, category, answer.trim(), sources);
        try {
            restTemplate.postForLocation(
                    portalServiceUrl + "/api/portal/log",
                    Map.of(
                            "farmerQuery", request.getQuery(),
                            "detectedLanguage", language,
                            "aiResponse", response.getResponse(),
                            "sourceReferences", response.getSources()));
        } catch (RestClientException exception) {
            log.warn("Advisory answer was generated but could not be saved to Portal history.", exception);
            List<String> responseSources = response.getSources();
            responseSources.add("[PORTAL-HISTORY] This answer could not be saved to conversation history.");
            response.setSources(responseSources);
        }
        return response;
    }

    @GetMapping("/weather")
    public Map<String, Object> weather(@RequestParam @NotBlank String district) {
        return currentWeather(district);
    }

    private Map<String, Object> currentWeather(String district) {
        try {
            JsonNode locations = restTemplate.getForObject(
                    "https://geocoding-api.open-meteo.com/v1/search?name={district}&count=10&language=en&format=json",
                    JsonNode.class,
                    district.trim());
            JsonNode results = locations == null ? null : locations.path("results");
            JsonNode place = null;
            if (results != null && results.isArray()) {
                for (JsonNode result : results) {
                    if ("IN".equalsIgnoreCase(result.path("country_code").asText())) {
                        place = result;
                        if (district.equalsIgnoreCase(result.path("name").asText())) {
                            break;
                        }
                    }
                }
            }
            if (place == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "No matching Indian district was found. Check the district name.");
            }

            String forecastUrl = String.format(Locale.ROOT,
                    "https://api.open-meteo.com/v1/forecast?latitude=%s&longitude=%s"
                            + "&current=temperature_2m,apparent_temperature,relative_humidity_2m,"
                            + "precipitation,rain,weather_code,wind_speed_10m"
                            + "&daily=weather_code,precipitation_probability_max,precipitation_sum,"
                            + "temperature_2m_max,temperature_2m_min,wind_speed_10m_max"
                            + "&forecast_days=7&timezone=auto",
                    place.path("latitude").asText(), place.path("longitude").asText());
            JsonNode forecast = restTemplate.getForObject(forecastUrl, JsonNode.class);
            JsonNode current = forecast == null ? null : forecast.path("current");
            JsonNode daily = forecast == null ? null : forecast.path("daily");
            if (current == null || current.isMissingNode() || daily == null || daily.isMissingNode()) {
                throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                        "The live weather provider returned incomplete forecast data.");
            }

            Map<String, Object> response = new LinkedHashMap<>();
            response.put("district", place.path("name").asText(district));
            response.put("state", place.path("admin1").asText(""));
            response.put("latitude", place.path("latitude").asDouble());
            response.put("longitude", place.path("longitude").asDouble());
            int weatherCode = current.path("weather_code").asInt(-1);
            response.put("weatherCode", weatherCode);
            response.put("summary", weatherDescription(weatherCode));
            response.put("temperatureC", current.path("temperature_2m").asDouble());
            response.put("feelsLikeC", current.path("apparent_temperature").asDouble());
            response.put("humidityPercent", current.path("relative_humidity_2m").asInt());
            response.put("rainMm", current.path("rain").asDouble());
            response.put("windKmh", current.path("wind_speed_10m").asDouble());
            int rainChance = daily.path("precipitation_probability_max").path(0).asInt(0);
            double todayHigh = daily.path("temperature_2m_max").path(0).asDouble();
            double windSpeed = current.path("wind_speed_10m").asDouble();
            response.put("rainChancePercent", rainChance);
            response.put("todayHighC", todayHigh);
            response.put("todayLowC", daily.path("temperature_2m_min").path(0).asDouble());
            response.put("weatherRiskCues", weatherRiskCues(weatherCode, rainChance, todayHigh, windSpeed));
            List<Map<String, Object>> weekForecast = new ArrayList<>();
            JsonNode dates = daily.path("time");
            for (int day = 0; day < dates.size(); day++) {
                int dailyCode = daily.path("weather_code").path(day).asInt(-1);
                int dailyRainChance = daily.path("precipitation_probability_max").path(day).asInt(0);
                double dailyHigh = daily.path("temperature_2m_max").path(day).asDouble();
                double dailyWind = daily.path("wind_speed_10m_max").path(day).asDouble();
                Map<String, Object> dayForecast = new LinkedHashMap<>();
                dayForecast.put("date", dates.path(day).asText());
                dayForecast.put("weatherCode", dailyCode);
                dayForecast.put("summary", weatherDescription(dailyCode));
                dayForecast.put("highC", dailyHigh);
                dayForecast.put("lowC", daily.path("temperature_2m_min").path(day).asDouble());
                dayForecast.put("rainChancePercent", dailyRainChance);
                dayForecast.put("rainMm", daily.path("precipitation_sum").path(day).asDouble());
                dayForecast.put("windMaxKmh", dailyWind);
                dayForecast.put("riskCues", weatherRiskCues(
                        dailyCode, dailyRainChance, dailyHigh, dailyWind));
                weekForecast.add(dayForecast);
            }
            response.put("forecast", weekForecast);
            response.put("source", "Live forecast from Open-Meteo. Forecasts are estimates, not official warnings.");
            response.put("updatedAt", current.path("time").asText());
            return response;
        } catch (RestClientException exception) {
            log.warn("Live weather lookup failed for district {}.", district, exception);
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                    "Live weather is temporarily unavailable. Please try again shortly.", exception);
        }
    }

    private List<String> weatherRiskCues(int weatherCode, int rainChance, double todayHigh, double windKmh) {
        List<String> cues = new ArrayList<>();
        if (weatherCode >= 95 && weatherCode <= 99) {
            cues.add("Thunderstorm conditions are in the forecast.");
        }
        if (rainChance >= 70) {
            cues.add("High rain probability in today's forecast.");
        }
        if (windKmh >= 50) {
            cues.add("Strong winds in the forecast.");
        }
        if (todayHigh >= 38) {
            cues.add("High temperature in today's forecast.");
        }
        return cues;
    }

    private String weatherDescription(int code) {
        return switch (code) {
            case 0 -> "Clear sky";
            case 1 -> "Mainly clear";
            case 2 -> "Partly cloudy";
            case 3 -> "Overcast";
            case 45, 48 -> "Fog";
            case 51, 53, 55 -> "Drizzle";
            case 56, 57 -> "Freezing drizzle";
            case 61, 63, 65 -> "Rain";
            case 66, 67 -> "Freezing rain";
            case 71, 73, 75, 77 -> "Snow";
            case 80, 81, 82 -> "Rain showers";
            case 85, 86 -> "Snow showers";
            case 95 -> "Thunderstorm";
            case 96, 99 -> "Thunderstorm with hail";
            default -> "Conditions unavailable";
        };
    }

    private String getFarmRecords(String district) {
        try {
            String context = restTemplate.getForObject(
                    portalServiceUrl + "/api/portal/farm-context?district={district}",
                    String.class,
                    district);
            return context == null ? "No farmer records are available." : context;
        } catch (RestClientException exception) {
            log.warn("Could not load farmer records from the Portal; continuing without that context.", exception);
            return "Farmer records could not be retrieved; do not make assumptions about the farm.";
        }
    }

    private List<String> sourcesFor(String category) {
        List<String> selected = new ArrayList<>();
        if ("market information".equals(category)) {
            selected.add(AGRICULTURE_NOTES.get(8));
        } else if ("government schemes".equals(category)) {
            selected.add(AGRICULTURE_NOTES.get(7));
        } else if ("pest management".equals(category)) {
            selected.add(AGRICULTURE_NOTES.get(1));
        } else if ("disease management".equals(category)) {
            selected.add(AGRICULTURE_NOTES.get(5));
        } else if ("irrigation".equals(category)) {
            selected.add(AGRICULTURE_NOTES.get(3));
        } else if ("soil health".equals(category)) {
            selected.add(AGRICULTURE_NOTES.get(4));
        } else if ("harvest and storage".equals(category)) {
            selected.add(AGRICULTURE_NOTES.get(6));
        } else if ("coconut care".equals(category)) {
            selected.add(AGRICULTURE_NOTES.get(9));
        } else {
            selected.add(AGRICULTURE_NOTES.get(0));
            selected.add(AGRICULTURE_NOTES.get(2));
            selected.add(AGRICULTURE_NOTES.get(9));
        }
        return selected;
    }

    private String classify(String query) {
        String text = query.toLowerCase(Locale.ROOT);
        if (containsAny(text, "weather", "rain", "rainfall", "forecast", "மழை", "बारिश", "वर्षा", "வானிலை", "മഴ", "ಮಳೆ", "వర్షం", "पाऊस")) return "weather";
        if (containsAny(text, "pest", "insect", "aphid", "caterpillar", "பூச்சி", "कीट", "കീടം", "ಕೀಟ", "పురుగు", "कीड")) return "pest management";
        if (containsAny(text, "disease", "blight", "fungus", "symptom", "நோய்", "रोग", "രോഗം", "ರೋಗ", "తెగులు", "रोग")) return "disease management";
        if (containsAny(text, "market", "price", "mandi", "sell", "भाव", "சந்தை", "വിപണി", "ಮಾರುಕಟ್ಟೆ", "మార్కెట్", "बाजार")) return "market information";
        if (containsAny(text, "scheme", "subsidy", "pm-kisan", "insurance", "योजना", "திட்டம்", "പദ്ധതി", "ಯೋಜನೆ", "పథకం", "योजना")) return "government schemes";
        if (containsAny(text, "irrigation", "water", "watering", "நீர்ப்பாசனம்", "தண்ணீர்", "सिंचाई", "पानी", "ജലസേചനം", "ನೀರಾವರಿ", "నీటిపారుదల", "पाणी")) return "irrigation";
        if (containsAny(text, "soil", "ph", "nutrient", "மண்", "मिट्टी", "मृदा", "മണ്ണ്", "ಮಣ್ಣು", "నేల", "माती")) return "soil health";
        if (containsAny(text, "harvest", "storage", "store", "post-harvest", "அறுவடை", "भंडारण", "कटाई", "വിളവെടുപ്പ്", "ಕೊಯ್ಲು", "కోత", "कापणी")) return "harvest and storage";
        if (containsAny(text, "coconut", "தேங்க", "नारियल", "തെങ്ങ", "ತೆಂಗ", "కొబ్బరి", "नारळ")) return "coconut care";
        return "crop and cultivation";
    }

    private boolean containsAny(String text, String... words) {
        for (String word : words) {
            if (text.contains(word)) return true;
        }
        return false;
    }
}
