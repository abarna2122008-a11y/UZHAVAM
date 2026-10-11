package com.hackathon.agri.advisory.controller;

import com.hackathon.agri.advisory.dto.AdvisoryRequest;
import com.hackathon.agri.advisory.dto.AdvisoryResponse;
import com.hackathon.agri.advisory.service.AdvisoryService;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.RestClientException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Set;

@RestController
@RequestMapping("/api/advisory")
public class AdvisoryController {

    private static final Set<String> SUPPORTED_LANGUAGES =
            Set.of("auto", "English", "Tamil", "Hindi", "Malayalam", "Kannada", "Telugu", "Marathi");

    private final AdvisoryService advisoryService;

    public AdvisoryController(AdvisoryService advisoryService) {
        this.advisoryService = advisoryService;
    }

    @PostMapping("/process")
    public AdvisoryResponse process(@RequestBody AdvisoryRequest request) {
        if (request == null || request.getQuery() == null || request.getQuery().isBlank()
                || request.getDistrict() == null || request.getDistrict().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Query and district are required.");
        }
        String preferredLanguage = request.getPreferredLanguage();
        if (preferredLanguage != null && !SUPPORTED_LANGUAGES.contains(preferredLanguage)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Choose a supported response language.");
        }
        try {
            return advisoryService.process(request);
        } catch (RestClientException exception) {
            throw new ResponseStatusException(
                    HttpStatus.SERVICE_UNAVAILABLE, exception.getMessage(), exception);
        }
    }
}
