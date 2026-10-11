package com.agri.ai.portal.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "pest_observations")
public class PestObservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 100)
    private String cropName;
    @Column(nullable = false, length = 100)
    private String symptom;
    @Column(nullable = false, length = 100)
    private String district;
    @Column(nullable = false)
    private LocalDate observedOn;
    @Column(nullable = false, length = 2000)
    private String observations;
    @Column(nullable = false, length = 1000)
    private String actionTaken;
    @Column(nullable = false)
    private boolean resolved;
    @Column(nullable = false)
    private Instant createdAt;

    public PestObservation() {
    }
    public Long getId() { return id; }
    public String getCropName() { return cropName; }
    public void setCropName(String cropName) { this.cropName = cropName; }
    public String getSymptom() { return symptom; }
    public void setSymptom(String symptom) { this.symptom = symptom; }
    public String getDistrict() { return district; }
    public void setDistrict(String district) { this.district = district; }
    public LocalDate getObservedOn() { return observedOn; }
    public void setObservedOn(LocalDate observedOn) { this.observedOn = observedOn; }
    public String getObservations() { return observations; }
    public void setObservations(String observations) { this.observations = observations; }
    public String getActionTaken() { return actionTaken; }
    public void setActionTaken(String actionTaken) { this.actionTaken = actionTaken; }
    public boolean isResolved() { return resolved; }
    public void setResolved(boolean resolved) { this.resolved = resolved; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
