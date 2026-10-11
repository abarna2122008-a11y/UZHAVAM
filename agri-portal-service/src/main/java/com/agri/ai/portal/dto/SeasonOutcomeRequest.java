package com.agri.ai.portal.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public class SeasonOutcomeRequest {
    @NotBlank @Size(max=100) private String cropName;
    @NotBlank @Size(max=80) private String seasonName;
    @NotNull private LocalDate completedOn;
    @NotNull @DecimalMin("0.00") @Digits(integer=10, fraction=2) private BigDecimal cultivationCost;
    @NotNull @DecimalMin("0.000") @Digits(integer=9, fraction=3) private BigDecimal yieldQuantity;
    @NotBlank @Size(max=20) private String yieldUnit;
    @NotNull @DecimalMin("0.00") @Digits(integer=10, fraction=2) private BigDecimal salesRevenue;
    @Size(max=1000) private String notes;

    public SeasonOutcomeRequest() {}
    public String getCropName() { return cropName; }
    public void setCropName(String value) { cropName=value; }
    public String getSeasonName() { return seasonName; }
    public void setSeasonName(String value) { seasonName=value; }
    public LocalDate getCompletedOn() { return completedOn; }
    public void setCompletedOn(LocalDate value) { completedOn=value; }
    public BigDecimal getCultivationCost() { return cultivationCost; }
    public void setCultivationCost(BigDecimal value) { cultivationCost=value; }
    public BigDecimal getYieldQuantity() { return yieldQuantity; }
    public void setYieldQuantity(BigDecimal value) { yieldQuantity=value; }
    public String getYieldUnit() { return yieldUnit; }
    public void setYieldUnit(String value) { yieldUnit=value; }
    public BigDecimal getSalesRevenue() { return salesRevenue; }
    public void setSalesRevenue(BigDecimal value) { salesRevenue=value; }
    public String getNotes() { return notes; }
    public void setNotes(String value) { notes=value; }
}
