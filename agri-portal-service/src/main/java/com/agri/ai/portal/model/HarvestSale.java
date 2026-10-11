package com.agri.ai.portal.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "harvest_sales")
public class HarvestSale {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 100)
    private String cropName;
    @Column(nullable = false, length = 120)
    private String buyerName;
    @Column(length = 120)
    private String marketName;
    @Column(nullable = false, precision = 12, scale = 3)
    private BigDecimal agreedQuantityQuintals;
    @Column(precision = 12, scale = 3)
    private BigDecimal weighedQuantityQuintals;
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal agreedPricePerQuintal;
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal transportCost;
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal deductions;
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amountPaid;
    @Column(nullable = false)
    private LocalDate saleDate;
    @Column(nullable = false, length = 1000)
    private String agreementNotes;
    @Column(nullable = false)
    private Instant createdAt;

    public HarvestSale() {
    }

    public Long getId() { return id; }
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
    public BigDecimal getWeighedQuantityQuintals() { return weighedQuantityQuintals; }
    public void setWeighedQuantityQuintals(BigDecimal weighedQuantityQuintals) {
        this.weighedQuantityQuintals = weighedQuantityQuintals;
    }
    public BigDecimal getAgreedPricePerQuintal() { return agreedPricePerQuintal; }
    public void setAgreedPricePerQuintal(BigDecimal agreedPricePerQuintal) {
        this.agreedPricePerQuintal = agreedPricePerQuintal;
    }
    public BigDecimal getTransportCost() { return transportCost; }
    public void setTransportCost(BigDecimal transportCost) { this.transportCost = transportCost; }
    public BigDecimal getDeductions() { return deductions; }
    public void setDeductions(BigDecimal deductions) { this.deductions = deductions; }
    public BigDecimal getAmountPaid() { return amountPaid; }
    public void setAmountPaid(BigDecimal amountPaid) { this.amountPaid = amountPaid; }
    public LocalDate getSaleDate() { return saleDate; }
    public void setSaleDate(LocalDate saleDate) { this.saleDate = saleDate; }
    public String getAgreementNotes() { return agreementNotes; }
    public void setAgreementNotes(String agreementNotes) { this.agreementNotes = agreementNotes; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public BigDecimal getExpectedGross() {
        return money(agreedQuantityQuintals.multiply(agreedPricePerQuintal));
    }

    public BigDecimal getExpectedNet() {
        return getExpectedGross().subtract(transportCost).subtract(deductions);
    }

    public BigDecimal getActualGross() {
        return weighedQuantityQuintals == null ? null : money(weighedQuantityQuintals.multiply(agreedPricePerQuintal));
    }

    public BigDecimal getActualNet() {
        BigDecimal actualGross = getActualGross();
        return actualGross == null ? null : actualGross.subtract(transportCost).subtract(deductions);
    }

    public BigDecimal getBalanceDue() {
        BigDecimal actualNet = getActualNet();
        return actualNet == null ? null : actualNet.subtract(amountPaid).max(BigDecimal.ZERO);
    }

    public String getSaleStatus() {
        if (weighedQuantityQuintals == null) return "Agreed";
        if (getBalanceDue().compareTo(BigDecimal.ZERO) == 0) return "Settled";
        return amountPaid.compareTo(BigDecimal.ZERO) > 0 ? "Part-paid" : "Awaiting payment";
    }

    private BigDecimal money(BigDecimal amount) {
        return amount.setScale(2, RoundingMode.HALF_UP);
    }
}
