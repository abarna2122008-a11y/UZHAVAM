package com.agri.ai.portal.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public class CommunityOfferRequest {
    @NotBlank private String offerType;
    @NotBlank @Size(max=160) private String title;
    @NotBlank @Size(max=120) private String contributor;
    @NotBlank @Size(max=120) private String district;
    @NotBlank @Size(max=2000) private String details;
    @NotNull @DecimalMin("0.001") @Digits(integer=8, fraction=2) private BigDecimal quantity;
    @NotBlank @Size(max=30) private String unit;
    @Size(max=30) private String contactPhone;
    private LocalDate availableDate;
    private LocalDate endDate;

    public CommunityOfferRequest() {}
    public String getOfferType() { return offerType; }
    public void setOfferType(String value) { offerType=value; }
    public String getTitle() { return title; }
    public void setTitle(String value) { title=value; }
    public String getContributor() { return contributor; }
    public void setContributor(String value) { contributor=value; }
    public String getDistrict() { return district; }
    public void setDistrict(String value) { district=value; }
    public String getDetails() { return details; }
    public void setDetails(String value) { details=value; }
    public BigDecimal getQuantity() { return quantity; }
    public void setQuantity(BigDecimal value) { quantity=value; }
    public String getUnit() { return unit; }
    public void setUnit(String value) { unit=value; }
    public String getContactPhone() { return contactPhone; }
    public void setContactPhone(String value) { contactPhone=value; }
    public LocalDate getAvailableDate() { return availableDate; }
    public void setAvailableDate(LocalDate value) { availableDate=value; }
    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate value) { endDate=value; }
}
