package com.fieldwise.advisory.service;

import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.Map;

@Service
public class MockWeatherService {

    private static final Map<String, String> DISTRICT_FORECASTS = Map.of(
            "thanjavur", "Demo forecast: daytime high 32 C; rain chance 20% over the next 24 hours.",
            "coimbatore", "Demo forecast: daytime high 29 C; rain chance 35% over the next 24 hours.",
            "pune", "Demo forecast: daytime high 27 C; rain chance 45% over the next 24 hours.",
            "madurai", "Demo forecast: daytime high 34 C; rain chance 10% over the next 24 hours.",
            "mysuru", "Demo forecast: daytime high 28 C; rain chance 40% over the next 24 hours.");

    public String forecast(String location) {
        return DISTRICT_FORECASTS.getOrDefault(
                location.trim().toLowerCase(Locale.ROOT),
                "No forecast is loaded for this district in the demo dataset. Do not infer temperature or rainfall.");
    }
}
