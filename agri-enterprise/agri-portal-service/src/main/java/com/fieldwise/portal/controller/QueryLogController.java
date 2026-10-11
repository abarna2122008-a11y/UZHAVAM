package com.fieldwise.portal.controller;

import com.fieldwise.portal.dto.InteractionLogRequest;
import com.fieldwise.portal.model.QueryLog;
import com.fieldwise.portal.repository.QueryLogRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/portal")
public class QueryLogController {

    private final QueryLogRepository queryLogRepository;

    public QueryLogController(QueryLogRepository queryLogRepository) {
        this.queryLogRepository = queryLogRepository;
    }

    @PostMapping("/log")
    @ResponseStatus(HttpStatus.CREATED)
    public QueryLog log(@Valid @RequestBody InteractionLogRequest request) {
        QueryLog queryLog = new QueryLog(
                request.getFarmerQuery(),
                request.getDetectedLanguage(),
                request.getAiResponse(),
                Instant.now(),
                request.getSourceReferences());
        return queryLogRepository.save(queryLog);
    }

    @GetMapping("/history")
    public List<QueryLog> history() {
        return queryLogRepository.findAllByOrderByTimestampDesc();
    }
}
