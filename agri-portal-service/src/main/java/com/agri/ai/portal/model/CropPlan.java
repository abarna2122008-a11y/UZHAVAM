package com.agri.ai.portal.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "crop_plans")
public class CropPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 100)
    private String cropName;
    @Column(length = 120)
    private String variety;
    @Column(nullable = false, length = 80)
    private String season;
    @Column(nullable = false)
    private LocalDate sowingDate;
    private LocalDate harvestDate;
    @Column(nullable = false, precision = 8, scale = 2)
    private BigDecimal areaAcres;
    @Column(nullable = false, length = 30)
    private String status;

    public CropPlan() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
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
