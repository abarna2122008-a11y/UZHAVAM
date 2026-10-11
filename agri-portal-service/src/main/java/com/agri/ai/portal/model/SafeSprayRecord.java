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
@Table(name = "safe_spray_records")
public class SafeSprayRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 100)
    private String cropName;
    @Column(nullable = false, length = 120)
    private String productName;
    @Column(length = 120)
    private String activeIngredient;
    @Column(nullable = false)
    private LocalDate appliedDate;
    @Column(nullable = false)
    private int preHarvestIntervalDays;
    private LocalDate plannedHarvestDate;
    @Column(nullable = false, length = 1000)
    private String labelNotes;
    @Column(nullable = false)
    private Instant createdAt;

    public SafeSprayRecord() {
    }
    public Long getId() { return id; }
    public String getCropName() { return cropName; }
    public void setCropName(String cropName) { this.cropName = cropName; }
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    public String getActiveIngredient() { return activeIngredient; }
    public void setActiveIngredient(String activeIngredient) { this.activeIngredient = activeIngredient; }
    public LocalDate getAppliedDate() { return appliedDate; }
    public void setAppliedDate(LocalDate appliedDate) { this.appliedDate = appliedDate; }
    public int getPreHarvestIntervalDays() { return preHarvestIntervalDays; }
    public void setPreHarvestIntervalDays(int preHarvestIntervalDays) { this.preHarvestIntervalDays = preHarvestIntervalDays; }
    public LocalDate getPlannedHarvestDate() { return plannedHarvestDate; }
    public void setPlannedHarvestDate(LocalDate plannedHarvestDate) { this.plannedHarvestDate = plannedHarvestDate; }
    public String getLabelNotes() { return labelNotes; }
    public void setLabelNotes(String labelNotes) { this.labelNotes = labelNotes; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public LocalDate getSafeHarvestDate() { return appliedDate.plusDays(preHarvestIntervalDays); }
    public boolean isHarvestDateSafe() {
        return plannedHarvestDate == null || !plannedHarvestDate.isBefore(getSafeHarvestDate());
    }
}
