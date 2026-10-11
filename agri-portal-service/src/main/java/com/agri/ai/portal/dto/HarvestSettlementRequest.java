package com.agri.ai.portal.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class HarvestSettlementRequest {

    @NotNull @DecimalMin("0.001") @Digits(integer = 9, fraction = 3)
    private BigDecimal weighedQuantityQuintals;
    @NotNull @DecimalMin("0.00") @Digits(integer = 10, fraction = 2)
    private BigDecimal deductions;
    @NotNull @DecimalMin("0.00") @Digits(integer = 10, fraction = 2)
    private BigDecimal amountPaid;

    public HarvestSettlementRequest() {
    }

    public BigDecimal getWeighedQuantityQuintals() { return weighedQuantityQuintals; }
    public void setWeighedQuantityQuintals(BigDecimal weighedQuantityQuintals) {
        this.weighedQuantityQuintals = weighedQuantityQuintals;
    }
    public BigDecimal getDeductions() { return deductions; }
    public void setDeductions(BigDecimal deductions) { this.deductions = deductions; }
    public BigDecimal getAmountPaid() { return amountPaid; }
    public void setAmountPaid(BigDecimal amountPaid) { this.amountPaid = amountPaid; }
}
