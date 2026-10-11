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
@Table(name = "community_offers")
public class CommunityOffer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 30)
    private String offerType;
    @Column(nullable = false, length = 160)
    private String title;
    @Column(nullable = false, length = 120)
    private String contributor;
    @Column(nullable = false, length = 120)
    private String district;
    @Column(nullable = false, length = 2000)
    private String details;
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal quantity;
    @Column(nullable = false, length = 30)
    private String unit;
    @Column(length = 30)
    private String contactPhone;
    private LocalDate availableDate;
    private LocalDate endDate;
    @Column(nullable = false, length = 20)
    private String status;
    @Column(nullable = false)
    private Instant createdAt;

    public CommunityOffer() {
    }
    public Long getId() { return id; }
    public String getOfferType() { return offerType; }
    public void setOfferType(String offerType) { this.offerType = offerType; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getContributor() { return contributor; }
    public void setContributor(String contributor) { this.contributor = contributor; }
    public String getDistrict() { return district; }
    public void setDistrict(String district) { this.district = district; }
    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }
    public BigDecimal getQuantity() { return quantity; }
    public void setQuantity(BigDecimal quantity) { this.quantity = quantity; }
    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
    public String getContactPhone() { return contactPhone; }
    public void setContactPhone(String contactPhone) { this.contactPhone = contactPhone; }
    public LocalDate getAvailableDate() { return availableDate; }
    public void setAvailableDate(LocalDate availableDate) { this.availableDate = availableDate; }
    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
