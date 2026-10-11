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
@Table(name = "reloop_listings")
public class ReLoopListing {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 120)
    private String materialName;
    @Column(nullable = false, length = 40)
    private String materialType;
    @Column(nullable = false, precision = 12, scale = 3)
    private BigDecimal quantity;
    @Column(nullable = false, length = 20)
    private String unit;
    @Column(precision = 12, scale = 2)
    private BigDecimal askingPrice;
    @Column(nullable = false, length = 120)
    private String district;
    @Column(nullable = false, length = 120)
    private String sellerName;
    @Column(length = 30)
    private String contactPhone;
    @Column(nullable = false, length = 1000)
    private String details;
    @Column(nullable = false, length = 20)
    private String status;
    private LocalDate availableFrom;
    @Column(length = 120)
    private String reservedBy;
    @Column(length = 30)
    private String reservationPhone;
    private LocalDate pickupDate;
    @Column(precision = 12, scale = 3)
    private BigDecimal collectedQuantity;
    @Column(length = 120)
    private String sellerPickupConfirmation;
    @Column(length = 120)
    private String buyerPickupConfirmation;
    private Instant collectedAt;
    @Column(nullable = false)
    private Instant createdAt;

    public ReLoopListing() {
    }

    public Long getId() { return id; }
    public String getMaterialName() { return materialName; }
    public void setMaterialName(String materialName) { this.materialName = materialName; }
    public String getMaterialType() { return materialType; }
    public void setMaterialType(String materialType) { this.materialType = materialType; }
    public BigDecimal getQuantity() { return quantity; }
    public void setQuantity(BigDecimal quantity) { this.quantity = quantity; }
    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
    public BigDecimal getAskingPrice() { return askingPrice; }
    public void setAskingPrice(BigDecimal askingPrice) { this.askingPrice = askingPrice; }
    public String getDistrict() { return district; }
    public void setDistrict(String district) { this.district = district; }
    public String getSellerName() { return sellerName; }
    public void setSellerName(String sellerName) { this.sellerName = sellerName; }
    public String getContactPhone() { return contactPhone; }
    public void setContactPhone(String contactPhone) { this.contactPhone = contactPhone; }
    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDate getAvailableFrom() { return availableFrom; }
    public void setAvailableFrom(LocalDate availableFrom) { this.availableFrom = availableFrom; }
    public String getReservedBy() { return reservedBy; }
    public void setReservedBy(String reservedBy) { this.reservedBy = reservedBy; }
    public String getReservationPhone() { return reservationPhone; }
    public void setReservationPhone(String reservationPhone) { this.reservationPhone = reservationPhone; }
    public LocalDate getPickupDate() { return pickupDate; }
    public void setPickupDate(LocalDate pickupDate) { this.pickupDate = pickupDate; }
    public BigDecimal getCollectedQuantity() { return collectedQuantity; }
    public void setCollectedQuantity(BigDecimal collectedQuantity) { this.collectedQuantity = collectedQuantity; }
    public String getSellerPickupConfirmation() { return sellerPickupConfirmation; }
    public void setSellerPickupConfirmation(String sellerPickupConfirmation) { this.sellerPickupConfirmation = sellerPickupConfirmation; }
    public String getBuyerPickupConfirmation() { return buyerPickupConfirmation; }
    public void setBuyerPickupConfirmation(String buyerPickupConfirmation) { this.buyerPickupConfirmation = buyerPickupConfirmation; }
    public Instant getCollectedAt() { return collectedAt; }
    public void setCollectedAt(Instant collectedAt) { this.collectedAt = collectedAt; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
