package com.fieldwise.farmer.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fieldwise.farmer.dto.WeatherSummary;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@Service
public class WeatherService {

    private static final String GEOCODING_URL = "https://geocoding-api.open-meteo.com/v1/search";
    private static final String FORECAST_URL = "https://api.open-meteo.com/v1/forecast";
    private final RestTemplate restTemplate;

    public WeatherService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public WeatherSummary getForecast(String district) {
        try {
            URI geocodingUri = UriComponentsBuilder.fromUriString(GEOCODING_URL)
                    .queryParam("name", district)
                    .queryParam("count", 1)
                    .queryParam("countryCode", "IN")
                    .queryParam("language", "en")
                    .queryParam("format", "json")
                    .build().encode().toUri();
            JsonNode geocoding = restTemplate.getForObject(geocodingUri, JsonNode.class);
            JsonNode place = geocoding == null ? null : geocoding.path("results").path(0);
            if (place == null || place.isMissingNode()) {
                return unavailable(district, "No matching Indian district was returned by Open-Meteo geocoding.");
            }

            URI forecastUri = UriComponentsBuilder.fromUriString(FORECAST_URL)
                    .queryParam("latitude", place.path("latitude").asDouble())
                    .queryParam("longitude", place.path("longitude").asDouble())
                    .queryParam("daily", "temperature_2m_max,precipitation_probability_max")
                    .queryParam("forecast_days", 5)
                    .queryParam("timezone", "auto")
                    .build().encode().toUri();
            JsonNode forecast = restTemplate.getForObject(forecastUri, JsonNode.class);
            JsonNode daily = forecast == null ? null : forecast.path("daily");
            if (daily == null || daily.path("time").isEmpty()) {
                return unavailable(district, "Open-Meteo returned no daily forecast values.");
            }

            List<WeatherSummary.DailyForecast> days = new ArrayList<>();
            for (int index = 0; index < daily.path("time").size(); index++) {
                days.add(new WeatherSummary.DailyForecast(
                        daily.path("time").path(index).asText(),
                        daily.path("temperature_2m_max").path(index).asText("unavailable"),
                        daily.path("precipitation_probability_max").path(index).asText("unavailable")));
            }
            String placeName = place.path("name").asText(district);
            return new WeatherSummary(
                    placeName,
                    true,
                    "Five-day forecast from Open-Meteo for " + placeName + ". Use local alerts before field operations.",
                    days,
                    "Open-Meteo forecast API: https://open-meteo.com/en/docs");
        } catch (RestClientException exception) {
            return unavailable(district, "Weather lookup failed: " + exception.getMessage());
        }
    }

    private WeatherSummary unavailable(String district, String message) {
        return new WeatherSummary(
                district, false, message, List.of(),
                "Open-Meteo forecast API: https://open-meteo.com/en/docs");
    }
}
