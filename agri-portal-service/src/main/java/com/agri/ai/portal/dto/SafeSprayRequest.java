package com.agri.ai.portal.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public class SafeSprayRequest {
    @NotBlank @Size(max = 100)
    private String cropName;
    @NotBlank @Size(max = 120)
    private String productName;
    @Size(max = 120)
    private String activeIngredient;
    @NotNull
    private LocalDate appliedDate;
    @Min(0) @Max(365)
    private int preHarvestIntervalDays;
    private LocalDate plannedHarvestDate;
    @Size(max = 1000)
    private String labelNotes;

    public SafeSprayRequest() {
    }
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
}
