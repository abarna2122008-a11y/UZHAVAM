package com.hackathon.agri.advisory.service;

import com.hackathon.agri.advisory.dto.AdvisoryRequest;
import com.hackathon.agri.advisory.dto.AdvisoryResponse;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
public class AdvisoryService {

    private final WeatherService weatherService;
    private final OllamaService ollamaService;

    public AdvisoryService(WeatherService weatherService, OllamaService ollamaService) {
        this.weatherService = weatherService;
        this.ollamaService = ollamaService;
    }

    public AdvisoryResponse process(AdvisoryRequest request) {
        String selectedLanguage = request.getPreferredLanguage();
        String language = selectedLanguage == null || selectedLanguage.isBlank()
                || "auto".equalsIgnoreCase(selectedLanguage)
                ? detectLanguage(request.getQuery())
                : selectedLanguage;
        String category = classifyQuery(request.getQuery());
        String knowledge = retrieveKnowledge(request.getQuery(), category);
        WeatherService.WeatherContext weather = weatherService.getForecast(request.getDistrict());
        String answer = ollamaService.generate(
                request.getQuery(), language, category, knowledge, weather.summary());

        List<String> sources = new ArrayList<>();
        sources.add(knowledgeReference(category));
        sources.add(weather.reference());
        return new AdvisoryResponse(language, category, answer, sources);
    }

    public String detectLanguage(String text) {
        boolean hasDevanagari = false;
        for (int index = 0; index < text.length(); index++) {
            char character = text.charAt(index);
            if (character >= '\u0B80' && character <= '\u0BFF') {
                return "Tamil";
            }
            if (character >= '\u0C00' && character <= '\u0C7F') {
                return "Telugu";
            }
            if (character >= '\u0C80' && character <= '\u0CFF') {
                return "Kannada";
            }
            if (character >= '\u0D00' && character <= '\u0D7F') {
                return "Malayalam";
            }
            if (character >= '\u0900' && character <= '\u097F') {
                hasDevanagari = true;
            }
        }

        if (hasDevanagari) {
            String lowerText = text.toLowerCase(Locale.ROOT);
            if (containsAny(lowerText, "माझ्या", "आहे", "शेत", "करा", "पिकासाठी", "कापूस")) {
                return "Marathi";
            }
            return "Hindi";
        }
        return "English";
    }

    public String classifyQuery(String text) {
        String query = text.toLowerCase(Locale.ROOT);
        if (containsAny(query, "market", "mandi", "price", "भाव", "बाजार", "சந்தை", "మార్కెట్")) {
            return "market information";
        }
        if (containsAny(query, "scheme", "subsidy", "pm-kisan", "government", "योजना", "अनुदान", "திட்டம்")) {
            return "government schemes";
        }
        if (containsAny(query, "what crop", "which crop", "crop should", "crop selection", "cultivate")) {
            return "crop selection and cultivation";
        }
        if (containsAny(query, "weather", "rain", "rainfall", "temperature", "மழை", "बारिश", "வானிலை")) {
            return "weather";
        }
        if (containsAny(query, "disease", "fungus", "blight", "spot", "நோய்", "रोग", "వ్యాధి")) {
            return "disease management";
        }
        if (containsAny(query, "pest", "insect", "aphid", "bollworm", "பூச்சி", "कीट", "పురుగు")) {
            return "pest management";
        }
        if (containsAny(query, "season", "sowing", "planting", "monsoon", "பருவம்", "मौसम")) {
            return "seasonal planning";
        }
        if (containsAny(query, "crop", "cultivate", "grow", "பயிர்", "फसल", "పంట", "ಬೆಳೆ", "വിള")) {
            return "crop selection and cultivation";
        }
        if (containsAny(query, "fertilizer", "irrigation", "soil", "pruning", "உரம்", "खाद", "सिंचाई")) {
            return "farming practices";
        }
        return "general agriculture";
    }

    public String retrieveKnowledge(String query, String category) {
        String text = query.toLowerCase(Locale.ROOT);
        if (containsWord(text, "rice") || containsAny(text, "paddy", "நெல்", "धान", "వరి", "ಅಕ್ಕి", "भात")) {
            return "Rice: use a locally recommended variety and planting window; keep field water manageable rather than continuously deep, and base fertilizer on soil testing. For suspected disease or pests, identify the cause before choosing treatment.";
        }
        if (containsAny(text, "wheat", "गेहूं", "गहू", "గోధుమ", "ಗೋಧಿ", "ഗോതമ്പ്")) {
            return "Wheat: select a variety suited to local climate and sowing date; use well-drained soil and base nutrient application on a soil test. Check local extension guidance for regional timing.";
        }
        if (containsAny(text, "cotton", "பருத்தி", "कपास", "कापूस", "పత్తి", "ಹತ್ತಿ", "പരുത്തി")) {
            return "Cotton: scout the field regularly for insect damage, conserve beneficial insects, and use locally approved integrated pest management. Confirm any pesticide choice and label instructions with an agricultural extension officer.";
        }
        if (containsAny(text, "tomato", "தக்காளி", "टमाटर", "टोमॅटो", "టమాట", "ಟೊಮೇಟೊ", "തക്കാളി")) {
            return "Tomato: maintain even soil moisture, avoid wetting foliage late in the day, and remove diseased plant material safely. Confirm disease identification before applying any treatment.";
        }
        return switch (category) {
            case "pest management" -> "Pest management: identify the pest and check the level of crop damage before acting. Prefer monitoring, field sanitation, and locally recommended integrated pest management; use only products approved for that crop and pest.";
            case "disease management" -> "Disease management: note the crop, affected plant parts, and how symptoms spread. Avoid moving infected material between fields and seek a local diagnosis before applying treatment.";
            case "weather" -> "Weather decisions: use the district forecast below as short-term context, not a guarantee. Check an official local warning before irrigation, spraying, or field work.";
            case "market information" -> "Market information: prices vary by market and date. Check the latest arrivals and prices for the specific mandi on the Government of India's e-NAM portal before deciding when or where to sell.";
            case "government schemes" -> "Government schemes: eligibility and application windows vary. Verify current requirements on the official PM-KISAN or agriculture department portal; do not share OTPs or pay unofficial agents.";
            case "seasonal planning" -> "Seasonal planning: match crop variety and sowing date to local rainfall, soil, and irrigation availability. Confirm district-specific recommendations with a nearby Krishi Vigyan Kendra.";
            case "crop selection and cultivation" -> "Crop selection: consider soil type, recent rainfall, irrigation access, season, and nearby market demand together. A district name alone is not enough to make a reliable crop recommendation.";
            case "farming practices" -> "Farming practices: test soil before deciding fertilizer rates, maintain irrigation suited to crop stage, and follow local extension recommendations for timing and safe application.";
            default -> "Agriculture guidance: identify the crop, growth stage, district, and observed issue to narrow the recommendation. Confirm crop-specific actions with a local Krishi Vigyan Kendra or agriculture officer.";
        };
    }

    private String knowledgeReference(String category) {
        if ("market information".equals(category)) {
            return "Agricultural Knowledge Base (demo summary); verify current prices at e-NAM: https://enam.gov.in/";
        }
        if ("government schemes".equals(category)) {
            return "Agricultural Knowledge Base (demo summary); verify eligibility at PM-KISAN: https://pmkisan.gov.in/";
        }
        return "Agricultural Knowledge Base (curated PoC notes; verify locally): ICAR KVK network https://kvk.icar.gov.in/";
    }

    private boolean containsAny(String text, String... terms) {
        for (String term : terms) {
            if (text.contains(term)) {
                return true;
            }
        }
        return false;
    }

    private boolean containsWord(String text, String word) {
        return text.matches("(?s).*\\b" + word + "\\b.*");
    }
}
