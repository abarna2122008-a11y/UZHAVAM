package com.hackathon.agri.advisory.service;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.springframework.http.HttpMethod.POST;
import static org.hamcrest.Matchers.containsString;

class OllamaServiceTest {

    @Test
    void sendsRetrievedContextToConfiguredLocalModel() {
        RestTemplate restTemplate = new RestTemplate();
        MockRestServiceServer server = MockRestServiceServer.createServer(restTemplate);
        server.expect(requestTo("http://localhost:11434/api/generate"))
                .andExpect(method(POST))
                .andExpect(content().string(containsString("soil-test notes")))
                .andExpect(content().string(containsString("Tamil")))
                .andRespond(withSuccess("{\"response\":\"மண் பரிசோதனை செய்யவும்.\"}", MediaType.APPLICATION_JSON));

        OllamaService service = new OllamaService(restTemplate, "http://localhost:11434", "test-model");
        String answer = service.generate(
                "என் பயிருக்கு என்ன செய்யலாம்?", "Tamil", "farming practices",
                "soil-test notes", "Weather data is unavailable.");

        assertEquals("மண் பரிசோதனை செய்யவும்.", answer);
        server.verify();
    }
}
