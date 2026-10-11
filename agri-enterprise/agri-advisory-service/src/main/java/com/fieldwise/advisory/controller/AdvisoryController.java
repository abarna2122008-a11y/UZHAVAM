package com.fieldwise.advisory.controller;

import com.fieldwise.advisory.dto.AdvisoryRequest;
import com.fieldwise.advisory.dto.AdvisoryResponse;
import com.fieldwise.advisory.service.AdvisoryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/advisory")
public class AdvisoryController {

    private final AdvisoryService advisoryService;

    public AdvisoryController(AdvisoryService advisoryService) {
        this.advisoryService = advisoryService;
    }

    @PostMapping("/process")
    public AdvisoryResponse process(@Valid @RequestBody AdvisoryRequest request) {
        try {
            return advisoryService.process(request);
        } catch (org.springframework.web.client.RestClientException exception) {
            throw new ResponseStatusException(
                    HttpStatus.SERVICE_UNAVAILABLE, "An internal service is unavailable.", exception);
        } catch (IllegalStateException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, exception.getMessage(), exception);
        }
    }
}
