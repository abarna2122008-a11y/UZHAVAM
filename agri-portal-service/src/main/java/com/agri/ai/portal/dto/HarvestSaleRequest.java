package com.agri.ai.portal.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public class HarvestSaleRequest {

    @NotBlank @Size(max = 100)
    private String cropName;
    @NotBlank @Size(max = 120)
    private String buyerName;
    @Size(max = 120)
    private String marketName;
    @NotNull @DecimalMin("0.001") @Digits(integer = 9, fraction = 3)
    private BigDecimal agreedQuantityQuintals;
    @NotNull @DecimalMin("0.01") @Digits(integer = 10, fraction = 2)
    private BigDecimal agreedPricePerQuintal;
    @NotNull @DecimalMin("0.00") @Digits(integer = 10, fraction = 2)
    private BigDecimal transportCost;
    @NotNull @DecimalMin("0.00") @Digits(integer = 10, fraction = 2)
    private BigDecimal deductions;
    @NotNull
    private LocalDate saleDate;
    @Size(max = 1000)
    private String agreementNotes;

    public HarvestSaleRequest() {
    }

    public String getCropName() { return cropName; }
    public void setCropName(String cropName) { this.cropName = cropName; }
    public String getBuyerName() { return buyerName; }
    public void setBuyerName(String buyerName) { this.buyerName = buyerName; }
    public String getMarketName() { return marketName; }
    public void setMarketName(String marketName) { this.marketName = marketName; }
    public BigDecimal getAgreedQuantityQuintals() { return agreedQuantityQuintals; }
    public void setAgreedQuantityQuintals(BigDecimal agreedQuantityQuintals) {
        this.agreedQuantityQuintals = agreedQuantityQuintals;
    }
    public BigDecimal getAgreedPricePerQuintal() { return agreedPricePerQuintal; }
    public void setAgreedPricePerQuintal(BigDecimal agreedPricePerQuintal) {
        this.agreedPricePerQuintal = agreedPricePerQuintal;
    }
    public BigDecimal getTransportCost() { return transportCost; }
    public void setTransportCost(BigDecimal transportCost) { this.transportCost = transportCost; }
    public BigDecimal getDeductions() { return deductions; }
    public void setDeductions(BigDecimal deductions) { this.deductions = deductions; }
    public LocalDate getSaleDate() { return saleDate; }
    public void setSaleDate(LocalDate saleDate) { this.saleDate = saleDate; }
    public String getAgreementNotes() { return agreementNotes; }
    public void setAgreementNotes(String agreementNotes) { this.agreementNotes = agreementNotes; }
}
