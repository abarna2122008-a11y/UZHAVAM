package com.hackathon.agri.portal.service;

import com.hackathon.agri.portal.dto.AdvisoryRequest;
import com.hackathon.agri.portal.dto.AdvisoryResponse;
import com.hackathon.agri.portal.dto.AnalyticsItem;
import com.hackathon.agri.portal.dto.AnalyticsSummary;
import com.hackathon.agri.portal.model.QueryLog;
import com.hackathon.agri.portal.repository.QueryLogRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class PortalService {

    private final RestTemplate restTemplate;
    private final QueryLogRepository queryLogRepository;
    private final String advisoryServiceUrl;

    public PortalService(
            RestTemplate restTemplate,
            QueryLogRepository queryLogRepository,
            @Value("${advisory.service.url}") String advisoryServiceUrl) {
        this.restTemplate = restTemplate;
        this.queryLogRepository = queryLogRepository;
        this.advisoryServiceUrl = advisoryServiceUrl;
    }

    public AdvisoryResponse getAdvice(String query, String district, String preferredLanguage) {
        AdvisoryResponse response = restTemplate.postForObject(
                advisoryServiceUrl + "/api/advisory/process",
                new AdvisoryRequest(query, district, preferredLanguage),
                AdvisoryResponse.class);

        if (response == null || response.getAnswer() == null || response.getDetectedLanguage() == null
                || response.getCategory() == null) {
            throw new IllegalStateException("The advisory service returned an incomplete response.");
        }

        queryLogRepository.save(new QueryLog(
                query, district, response.getDetectedLanguage(), response.getCategory(), response.getAnswer()));
        return response;
    }

    public List<QueryLog> getRecentQueries() {
        return queryLogRepository.findTop20ByOrderByCreatedTimeDesc();
    }

    public AnalyticsSummary getAnalytics() {
        List<QueryLog> interactions = queryLogRepository.findAll();
        return new AnalyticsSummary(
                interactions.size(),
                countDistinct(interactions, QueryLog::getCategory),
                countDistinct(interactions, QueryLog::getDetectedLanguage),
                countDistinct(interactions, QueryLog::getDistrict),
                groupBy(interactions, QueryLog::getCategory),
                groupBy(interactions, QueryLog::getDetectedLanguage),
                groupBy(interactions, QueryLog::getDistrict));
    }

    private int countDistinct(List<QueryLog> interactions, Function<QueryLog, String> value) {
        return (int) interactions.stream().map(value).distinct().count();
    }

    private List<AnalyticsItem> groupBy(List<QueryLog> interactions, Function<QueryLog, String> value) {
        Map<String, Long> counts = interactions.stream()
                .map(value)
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
        long total = interactions.size();

        return counts.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue(Comparator.reverseOrder())
                        .thenComparing(Map.Entry.comparingByKey(String.CASE_INSENSITIVE_ORDER)))
                .map(entry -> new AnalyticsItem(
                        entry.getKey(),
                        entry.getValue(),
                        total == 0 ? 0 : (int) Math.round(entry.getValue() * 100.0 / total)))
                .toList();
    }
}
