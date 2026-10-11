package com.agri.ai.portal.model;

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
@Table(name = "plant_diagnoses")
public class PlantDiagnosis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 120)
    private String cropName;
    @Column(nullable = false, length = 120)
    private String district;
    @Column(nullable = false, length = 40)
    private String language;
    @Column(nullable = false, length = 240)
    private String cropIdentified;
    @Column(nullable = false, length = 500)
    private String likelyIssue;
    @Column(nullable = false, length = 20)
    private String confidence;
    @Column(nullable = false, length = 20)
    private String severity;
    @Column(nullable = false, columnDefinition = "text")
    private String observations;
    @Column(nullable = false, columnDefinition = "text")
    private String recommendations;
    @Column(nullable = false, length = 160)
    private String imageName;
    @Column(nullable = false)
    private Instant createdAt;
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "plant_diagnosis_sources", joinColumns = @JoinColumn(name = "diagnosis_id"))
    @Column(name = "source_reference", nullable = false, length = 500)
    private List<String> sourceReferences = new ArrayList<>();

    public PlantDiagnosis() {
    }

    public Long getId() { return id; }
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
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public List<String> getSourceReferences() { return new ArrayList<>(sourceReferences); }
    public void setSourceReferences(List<String> sourceReferences) {
        this.sourceReferences = new ArrayList<>(sourceReferences);
    }
}
