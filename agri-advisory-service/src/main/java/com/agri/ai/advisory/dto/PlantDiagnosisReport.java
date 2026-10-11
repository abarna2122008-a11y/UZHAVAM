package com.agri.ai.advisory.dto;

import java.util.ArrayList;
import java.util.List;

public class PlantDiagnosisReport {

    private String cropName;
    private String district;
    private String language;
    private String cropIdentified;
    private String likelyIssue;
    private String confidence;
    private String severity;
    private String observations;
    private String recommendations;
    private String imageName;
    private List<String> sourceReferences = new ArrayList<>();

    public PlantDiagnosisReport() {
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
        this.sourceReferences = new ArrayList<>(sourceReferences);
    }
}
