package com.agri.ai.portal.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ReLoopListingRequest {
    @NotBlank @Size(max = 120)
    private String materialName;
    @NotBlank
    private String materialType;
    @NotNull @DecimalMin("0.001") @Digits(integer = 9, fraction = 3)
    private BigDecimal quantity;
    @NotBlank
    private String unit;
    @DecimalMin("0.00") @Digits(integer = 10, fraction = 2)
    private BigDecimal askingPrice;
    @NotBlank @Size(max = 120)
    private String district;
    @NotBlank @Size(max = 120)
    private String sellerName;
    @Size(max = 30)
    private String contactPhone;
    @Size(max = 1000)
    private String details;
    private LocalDate availableFrom;

    public ReLoopListingRequest() {
    }
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
    public LocalDate getAvailableFrom() { return availableFrom; }
    public void setAvailableFrom(LocalDate availableFrom) { this.availableFrom = availableFrom; }
}
