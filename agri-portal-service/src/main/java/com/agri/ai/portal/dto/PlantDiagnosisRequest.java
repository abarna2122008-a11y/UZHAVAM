package com.agri.ai.portal.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.ArrayList;
import java.util.List;

public class PlantDiagnosisRequest {

    @NotBlank
    @Size(max = 120)
    private String cropName;
    @NotBlank
    @Size(max = 120)
    private String district;
    @NotBlank
    @Size(max = 40)
    private String language;
    @NotBlank
    @Size(max = 240)
    private String cropIdentified;
    @NotBlank
    @Size(max = 500)
    private String likelyIssue;
    @NotBlank
    @Size(max = 20)
    private String confidence;
    @NotBlank
    @Size(max = 20)
    private String severity;
    @NotBlank
    @Size(max = 2000)
    private String observations;
    @NotBlank
    @Size(max = 4000)
    private String recommendations;
    @NotBlank
    @Size(max = 160)
    private String imageName;
    private List<String> sourceReferences = new ArrayList<>();

    public PlantDiagnosisRequest() {
    }

    public String getCropName() { return cropName; }
    public void setCropName(String cropName) { this.cropName = cropName; }
    public String getDistrict() { return district; }
    public void setDistrict(String district) { this.district = district; }
    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }
    public String getCropIdentified() { return cropIdentified; }
    public void setCropIdentified(String cropIdentified) { this.cropIdentified = cropIdentified; }
    public String getLikelyIssue() { return likelyIssue; }
    public void setLikelyIssue(String likelyIssue) { this.likelyIssue = likelyIssue; }
    public String getConfidence() { return confidence; }
    public void setConfidence(String confidence) { this.confidence = confidence; }
    public String getSeverity() { return severity; }
    public void setSeverity(String severity) { this.severity = severity; }
    public String getObservations() { return observations; }
    public void setObservations(String observations) { this.observations = observations; }
    public String getRecommendations() { return recommendations; }
    public void setRecommendations(String recommendations) { this.recommendations = recommendations; }
    public String getImageName() { return imageName; }
    public void setImageName(String imageName) { this.imageName = imageName; }
    public List<String> getSourceReferences() { return new ArrayList<>(sourceReferences); }
    public void setSourceReferences(List<String> sourceReferences) {
        this.sourceReferences = sourceReferences == null ? new ArrayList<>() : new ArrayList<>(sourceReferences);
    }
}
