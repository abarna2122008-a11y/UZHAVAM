package com.agri.ai.portal.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public class FarmerProfileRequest {

    @NotBlank @Size(max = 120)
    private String farmerName;
    @Size(max = 120)
    private String farmName;
    @NotBlank @Size(max = 120)
    private String village;
    @NotBlank @Size(max = 120)
    private String district;
    @NotBlank @Size(max = 120)
    private String state;
    @Size(max = 100)
    private String soilType;
    @NotNull @DecimalMin("0.01")
    private BigDecimal landAcres;
    @NotBlank @Size(max = 100)
    private String irrigationSource;
    @NotBlank @Size(max = 40)
    private String preferredLanguage;

    public FarmerProfileRequest() {
    }

    public String getFarmerName() { return farmerName; }
    public void setFarmerName(String farmerName) { this.farmerName = farmerName; }
    public String getFarmName() { return farmName; }
    public void setFarmName(String farmName) { this.farmName = farmName; }
    public String getVillage() { return village; }
    public void setVillage(String village) { this.village = village; }
    public String getDistrict() { return district; }
    public void setDistrict(String district) { this.district = district; }
    public String getState() { return state; }
    public void setState(String state) { this.state = state; }
    public String getSoilType() { return soilType; }
    public void setSoilType(String soilType) { this.soilType = soilType; }
    public BigDecimal getLandAcres() { return landAcres; }
    public void setLandAcres(BigDecimal landAcres) { this.landAcres = landAcres; }
    public String getIrrigationSource() { return irrigationSource; }
    public void setIrrigationSource(String irrigationSource) { this.irrigationSource = irrigationSource; }
    public String getPreferredLanguage() { return preferredLanguage; }
    public void setPreferredLanguage(String preferredLanguage) { this.preferredLanguage = preferredLanguage; }
}
