package com.fieldwise.farmer.model;

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

    private LocalDate expectedHarvestDate;

    @Column(nullable = false, precision = 8, scale = 2)
    private BigDecimal areaAcres;

    @Column(nullable = false, length = 30)
    private String status;

    public CropPlan() {
    }

    public CropPlan(
            Long id,
            String cropName,
            String variety,
            String season,
            LocalDate sowingDate,
            LocalDate expectedHarvestDate,
            BigDecimal areaAcres,
            String status) {
        this.id = id;
        this.cropName = cropName;
        this.variety = variety;
        this.season = season;
        this.sowingDate = sowingDate;
        this.expectedHarvestDate = expectedHarvestDate;
        this.areaAcres = areaAcres;
        this.status = status;
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
    public LocalDate getExpectedHarvestDate() { return expectedHarvestDate; }
    public void setExpectedHarvestDate(LocalDate expectedHarvestDate) { this.expectedHarvestDate = expectedHarvestDate; }
    public BigDecimal getAreaAcres() { return areaAcres; }
    public void setAreaAcres(BigDecimal areaAcres) { this.areaAcres = areaAcres; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
