package com.fieldwise.advisory.dto;

import java.util.ArrayList;
import java.util.List;

public class PortalLogRequest {

    private String farmerQuery;
    private String detectedLanguage;
    private String aiResponse;
    private List<String> sourceReferences = new ArrayList<>();

    public PortalLogRequest() {
    }

    public PortalLogRequest(String farmerQuery, String detectedLanguage, String aiResponse, List<String> sourceReferences) {
        this.farmerQuery = farmerQuery;
        this.detectedLanguage = detectedLanguage;
        this.aiResponse = aiResponse;
        this.sourceReferences = new ArrayList<>(sourceReferences);
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

    public List<String> getSourceReferences() {
        return new ArrayList<>(sourceReferences);
    }

    public void setSourceReferences(List<String> sourceReferences) {
        this.sourceReferences = new ArrayList<>(sourceReferences);
    }
}
