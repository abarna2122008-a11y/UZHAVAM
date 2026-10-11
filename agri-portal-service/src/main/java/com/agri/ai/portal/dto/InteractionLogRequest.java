package com.agri.ai.portal.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.ArrayList;
import java.util.List;

public class InteractionLogRequest {

    @NotBlank @Size(max = 2000)
    private String farmerQuery;
    @NotBlank @Size(max = 40)
    private String detectedLanguage;
    @NotBlank
    private String aiResponse;
    @NotEmpty
    private List<@NotBlank String> sourceReferences = new ArrayList<>();

    public InteractionLogRequest() {
    }

    public String getFarmerQuery() { return farmerQuery; }
    public void setFarmerQuery(String farmerQuery) { this.farmerQuery = farmerQuery; }
    public String getDetectedLanguage() { return detectedLanguage; }
    public void setDetectedLanguage(String detectedLanguage) { this.detectedLanguage = detectedLanguage; }
    public String getAiResponse() { return aiResponse; }
    public void setAiResponse(String aiResponse) { this.aiResponse = aiResponse; }
    public List<String> getSourceReferences() { return new ArrayList<>(sourceReferences); }
    public void setSourceReferences(List<String> sourceReferences) {
        this.sourceReferences = new ArrayList<>(sourceReferences);
    }
}
