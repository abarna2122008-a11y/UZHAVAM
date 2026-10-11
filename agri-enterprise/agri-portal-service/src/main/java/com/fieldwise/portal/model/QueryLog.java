package com.fieldwise.portal.model;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "query_logs")
public class QueryLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 2000)
    private String farmerQuery;

    @Column(nullable = false, length = 40)
    private String detectedLanguage;

    @Column(nullable = false, columnDefinition = "text")
    private String aiResponse;

    @Column(name = "created_at", nullable = false)
    private Instant timestamp;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "query_source_references", joinColumns = @JoinColumn(name = "query_log_id"))
    @Column(name = "source_reference", nullable = false, length = 1000)
    private List<String> sourceReferences = new ArrayList<>();

    public QueryLog() {
    }

    public QueryLog(
            String farmerQuery,
            String detectedLanguage,
            String aiResponse,
            Instant timestamp,
            List<String> sourceReferences) {
        this.farmerQuery = farmerQuery;
        this.detectedLanguage = detectedLanguage;
        this.aiResponse = aiResponse;
        this.timestamp = timestamp;
        this.sourceReferences = new ArrayList<>(sourceReferences);
    }

    public Long getId() {
        return id;
    }

    public String getFarmerQuery() {
        return farmerQuery;
    }

    public void setFarmerQuery(String farmerQuery) {
        this.farmerQuery = farmerQuery;
    }

    public String getDetectedLanguage() {
        return detectedLanguage;
    }

    public void setDetectedLanguage(String detectedLanguage) {
        this.detectedLanguage = detectedLanguage;
    }

    public String getAiResponse() {
        return aiResponse;
    }

    public void setAiResponse(String aiResponse) {
        this.aiResponse = aiResponse;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public List<String> getSourceReferences() {
        return new ArrayList<>(sourceReferences);
    }

    public void setSourceReferences(List<String> sourceReferences) {
        this.sourceReferences = new ArrayList<>(sourceReferences);
    }
}
