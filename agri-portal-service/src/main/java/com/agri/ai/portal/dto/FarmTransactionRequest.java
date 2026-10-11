package com.agri.ai.portal.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public class FarmTransactionRequest {

    @NotBlank
    private String entryType;
    @NotBlank @Size(max = 100)
    private String category;
    @Size(max = 100)
    private String cropName;
    @NotNull @DecimalMin("0.01")
    private BigDecimal amount;
    @NotNull
    private LocalDate entryDate;
    @Size(max = 500)
    private String notes;

    public FarmTransactionRequest() {
    }

    public String getEntryType() { return entryType; }
    public void setEntryType(String entryType) { this.entryType = entryType; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getCropName() { return cropName; }
    public void setCropName(String cropName) { this.cropName = cropName; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public LocalDate getEntryDate() { return entryDate; }
    public void setEntryDate(LocalDate entryDate) { this.entryDate = entryDate; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
