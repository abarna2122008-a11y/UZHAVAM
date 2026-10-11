package com.agri.ai.portal.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public class CropPlanRequest {

    @NotBlank @Size(max = 100)
    private String cropName;
    @Size(max = 120)
    private String variety;
    @NotBlank @Size(max = 80)
    private String season;
    @NotNull
    private LocalDate sowingDate;
    private LocalDate harvestDate;
    @NotNull @DecimalMin("0.01")
    private BigDecimal areaAcres;
    @NotBlank @Size(max = 30)
    private String status;

    public CropPlanRequest() {
    }

    public String getCropName() { return cropName; }
    public void setCropName(String cropName) { this.cropName = cropName; }
    public String getVariety() { return variety; }
    public void setVariety(String variety) { this.variety = variety; }
    public String getSeason() { return season; }
    public void setSeason(String season) { this.season = season; }
    public LocalDate getSowingDate() { return sowingDate; }
    public void setSowingDate(LocalDate sowingDate) { this.sowingDate = sowingDate; }
    public LocalDate getHarvestDate() { return harvestDate; }
    public void setHarvestDate(LocalDate harvestDate) { this.harvestDate = harvestDate; }
    public BigDecimal getAreaAcres() { return areaAcres; }
    public void setAreaAcres(BigDecimal areaAcres) { this.areaAcres = areaAcres; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
