package com.hackathon.agri.advisory.service;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.springframework.http.HttpMethod.GET;
import static org.hamcrest.Matchers.startsWith;

class WeatherServiceTest {

    @Test
    void buildsForecastContextFromGeocodingAndForecastResponses() {
        RestTemplate restTemplate = new RestTemplate();
        MockRestServiceServer server = MockRestServiceServer.createServer(restTemplate);
        server.expect(requestTo(startsWith("https://geocoding-api.open-meteo.com/v1/search")))
                .andExpect(method(GET))
                .andRespond(withSuccess(
                        "{\"results\":[{\"name\":\"Thanjavur\",\"latitude\":10.78,\"longitude\":79.13}]}",
                        MediaType.APPLICATION_JSON));
        server.expect(requestTo(startsWith("https://api.open-meteo.com/v1/forecast")))
                .andExpect(method(GET))
                .andRespond(withSuccess(
                        "{\"daily\":{\"time\":[\"2026-10-10\"],\"temperature_2m_max\":[31.2],"
                                + "\"precipitation_probability_max\":[45]}}",
                        MediaType.APPLICATION_JSON));

        WeatherService.WeatherContext context = new WeatherService(restTemplate).getForecast("Thanjavur");

        assertTrue(context.summary().contains("Thanjavur"));
        assertTrue(context.summary().contains("31.2°C"));
        assertTrue(context.summary().contains("45%"));
        assertTrue(context.reference().contains("Open-Meteo"));
        server.verify();
    }
}
