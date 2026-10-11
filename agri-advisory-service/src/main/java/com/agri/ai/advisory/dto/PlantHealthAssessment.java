package com.agri.ai.advisory.dto;

public class PlantHealthAssessment {

    private String cropIdentified;
    private String likelyIssue;
    private String confidence;
    private String severity;
    private String observations;
    private String recommendations;

    public PlantHealthAssessment() {
    }

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
}
