package com.hackathon.agri.advisory.service;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Service
public class WeatherService {

    private static final String GEOCODING_URL = "https://geocoding-api.open-meteo.com/v1/search";
    private static final String FORECAST_URL = "https://api.open-meteo.com/v1/forecast";
    private static final String WEATHER_SOURCE = "Open-Meteo";

    private final RestTemplate restTemplate;

    public WeatherService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public WeatherContext getForecast(String district) {
        try {
            URI geocodingUri = UriComponentsBuilder.fromUriString(GEOCODING_URL)
                    .queryParam("name", district)
                    .queryParam("count", 1)
                    .queryParam("language", "en")
                    .queryParam("countryCode", "IN")
                    .queryParam("format", "json")
                    .build()
                    .encode()
                    .toUri();
            JsonNode geocoding = restTemplate.getForObject(geocodingUri, JsonNode.class);
            JsonNode location = geocoding == null ? null : geocoding.path("results").path(0);
            if (location == null || location.isMissingNode()) {
                return unavailable(district, "No matching location was returned by the geocoding service.");
            }

            double latitude = location.path("latitude").asDouble();
            double longitude = location.path("longitude").asDouble();
            URI forecastUri = UriComponentsBuilder.fromUriString(FORECAST_URL)
                    .queryParam("latitude", latitude)
                    .queryParam("longitude", longitude)
                    .queryParam("daily", "temperature_2m_max,precipitation_probability_max")
                    .queryParam("forecast_days", 3)
                    .queryParam("timezone", "auto")
                    .build()
                    .encode()
                    .toUri();
            JsonNode forecast = restTemplate.getForObject(forecastUri, JsonNode.class);
            JsonNode daily = forecast == null ? null : forecast.path("daily");
            if (daily == null || daily.path("time").isEmpty()) {
                return unavailable(district, "The weather provider returned no daily forecast.");
            }

            String place = location.path("name").asText(district);
            String summary = "Open-Meteo 3-day forecast for " + place + ": "
                    + daily.path("time").path(0).asText("date unavailable") + " high "
                    + daily.path("temperature_2m_max").path(0).asText("unavailable") + "°C; maximum precipitation probability "
                    + daily.path("precipitation_probability_max").path(0).asText("unavailable") + "%.";
            return new WeatherContext(summary,
                    "Open-Meteo forecast for " + place + " (3 days): " + forecastUri);
        } catch (RestClientException exception) {
            return unavailable(district, "Open-Meteo could not be reached: " + exception.getMessage());
        }
    }

    private WeatherContext unavailable(String district, String reason) {
        return new WeatherContext(
                "Weather data is unavailable for " + district + ". " + reason,
                WEATHER_SOURCE + " (unavailable for " + district + "): " + reason);
    }

    public record WeatherContext(String summary, String reference) {
    }
}
