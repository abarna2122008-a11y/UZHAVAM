package com.fieldwise.farmer.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "farmer_profiles")
public class FarmerProfile {

    @Id
    private Long id = 1L;

    @Column(nullable = false, length = 120)
    private String farmerName;

    @Column(nullable = false, length = 120)
    private String farmName;

    @Column(nullable = false, length = 120)
    private String village;

    @Column(nullable = false, length = 120)
    private String district;

    @Column(nullable = false, length = 120)
    private String state;

    @Column(nullable = false, length = 100)
    private String soilType;

    @Column(nullable = false, precision = 8, scale = 2)
    private BigDecimal landAcres;

    @Column(nullable = false, length = 100)
    private String irrigationSource;

    @Column(nullable = false, length = 40)
    private String preferredLanguage;

    @Column(nullable = false)
    private Instant updatedAt;

    public FarmerProfile() {
    }

    public FarmerProfile(
            Long id,
            String farmerName,
            String farmName,
            String village,
            String district,
            String state,
            String soilType,
            BigDecimal landAcres,
            String irrigationSource,
            String preferredLanguage,
            Instant updatedAt) {
        this.id = id;
        this.farmerName = farmerName;
        this.farmName = farmName;
        this.village = village;
        this.district = district;
        this.state = state;
        this.soilType = soilType;
        this.landAcres = landAcres;
        this.irrigationSource = irrigationSource;
        this.preferredLanguage = preferredLanguage;
        this.updatedAt = updatedAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
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
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
