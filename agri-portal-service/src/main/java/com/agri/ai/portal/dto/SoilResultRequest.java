package com.agri.ai.portal.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public class SoilResultRequest {
    @NotBlank @Size(max = 100)
    private String testType;
    @Size(max = 100)
    private String cropName;
    @NotBlank @Size(max = 100)
    private String fieldName;
    @NotNull @DecimalMin("0.0000") @Digits(integer = 8, fraction = 4)
    private BigDecimal value;
    @NotBlank @Size(max = 30)
    private String unit;
    @NotBlank @Size(max = 20)
    private String method;
    @NotNull
    private LocalDate testedOn;
    @Size(max = 1000)
    private String notes;

    public SoilResultRequest() {
    }
    public String getTestType() { return testType; }
    public void setTestType(String testType) { this.testType = testType; }
    public String getCropName() { return cropName; }
    public void setCropName(String cropName) { this.cropName = cropName; }
    public String getFieldName() { return fieldName; }
    public void setFieldName(String fieldName) { this.fieldName = fieldName; }
    public BigDecimal getValue() { return value; }
    public void setValue(BigDecimal value) { this.value = value; }
    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
    public String getMethod() { return method; }
    public void setMethod(String method) { this.method = method; }
    public LocalDate getTestedOn() { return testedOn; }
    public void setTestedOn(LocalDate testedOn) { this.testedOn = testedOn; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
