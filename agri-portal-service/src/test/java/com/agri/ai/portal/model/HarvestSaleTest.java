package com.agri.ai.portal.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HarvestSaleTest {

    @Test
    void calculatesExpectedAndActualNetAndOutstandingBalance() {
        HarvestSale sale = new HarvestSale();
        sale.setAgreedQuantityQuintals(new BigDecimal("10"));
        sale.setWeighedQuantityQuintals(new BigDecimal("9.5"));
        sale.setAgreedPricePerQuintal(new BigDecimal("2500"));
        sale.setTransportCost(new BigDecimal("500"));
        sale.setDeductions(new BigDecimal("250"));
        sale.setAmountPaid(new BigDecimal("20000"));

        assertEquals(new BigDecimal("24250.00"), sale.getExpectedNet());
        assertEquals(new BigDecimal("23000.00"), sale.getActualNet());
        assertEquals(new BigDecimal("3000.00"), sale.getBalanceDue());
        assertEquals("Part-paid", sale.getSaleStatus());
    }

    @Test
    void marksDealSettledWhenActualNetIsPaid() {
        HarvestSale sale = new HarvestSale();
        sale.setAgreedQuantityQuintals(new BigDecimal("4"));
        sale.setWeighedQuantityQuintals(new BigDecimal("4"));
        sale.setAgreedPricePerQuintal(new BigDecimal("1800"));
        sale.setTransportCost(new BigDecimal("200"));
        sale.setDeductions(new BigDecimal("100"));
        sale.setAmountPaid(new BigDecimal("6900"));

        assertEquals(BigDecimal.ZERO.setScale(2), sale.getBalanceDue());
        assertEquals("Settled", sale.getSaleStatus());
    }
}
