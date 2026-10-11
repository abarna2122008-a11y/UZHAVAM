package com.agri.ai.portal.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "season_outcomes")
public class SeasonOutcome {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 100)
    private String cropName;
    @Column(nullable = false, length = 80)
    private String seasonName;
    @Column(nullable = false)
    private LocalDate completedOn;
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal cultivationCost;
    @Column(nullable = false, precision = 12, scale = 3)
    private BigDecimal yieldQuantity;
    @Column(nullable = false, length = 20)
    private String yieldUnit;
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal salesRevenue;
    @Column(nullable = false, length = 1000)
    private String notes;
    @Column(nullable = false)
    private Instant createdAt;

    public SeasonOutcome() {
    }
    public Long getId() { return id; }
    public String getCropName() { return cropName; }
    public void setCropName(String cropName) { this.cropName = cropName; }
    public String getSeasonName() { return seasonName; }
    public void setSeasonName(String seasonName) { this.seasonName = seasonName; }
    public LocalDate getCompletedOn() { return completedOn; }
    public void setCompletedOn(LocalDate completedOn) { this.completedOn = completedOn; }
    public BigDecimal getCultivationCost() { return cultivationCost; }
    public void setCultivationCost(BigDecimal cultivationCost) { this.cultivationCost = cultivationCost; }
    public BigDecimal getYieldQuantity() { return yieldQuantity; }
    public void setYieldQuantity(BigDecimal yieldQuantity) { this.yieldQuantity = yieldQuantity; }
    public String getYieldUnit() { return yieldUnit; }
    public void setYieldUnit(String yieldUnit) { this.yieldUnit = yieldUnit; }
    public BigDecimal getSalesRevenue() { return salesRevenue; }
    public void setSalesRevenue(BigDecimal salesRevenue) { this.salesRevenue = salesRevenue; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public BigDecimal getNetOutcome() { return salesRevenue.subtract(cultivationCost); }
}
