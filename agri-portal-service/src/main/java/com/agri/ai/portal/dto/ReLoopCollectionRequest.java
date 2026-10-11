package com.agri.ai.portal.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public class ReLoopCollectionRequest {
    @NotNull
    @DecimalMin("0.001")
    private BigDecimal collectedQuantity;
    @NotBlank
    @Size(max = 120)
    private String sellerConfirmation;
    @NotBlank
    @Size(max = 120)
    private String buyerConfirmation;

    public ReLoopCollectionRequest() {
    }

    public BigDecimal getCollectedQuantity() { return collectedQuantity; }
    public void setCollectedQuantity(BigDecimal collectedQuantity) { this.collectedQuantity = collectedQuantity; }
    public String getSellerConfirmation() { return sellerConfirmation; }
    public void setSellerConfirmation(String sellerConfirmation) { this.sellerConfirmation = sellerConfirmation; }
    public String getBuyerConfirmation() { return buyerConfirmation; }
    public void setBuyerConfirmation(String buyerConfirmation) { this.buyerConfirmation = buyerConfirmation; }
}
