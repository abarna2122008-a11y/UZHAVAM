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
@Table(name = "soil_results")
public class SoilResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 100)
    private String testType;
    @Column(length = 100)
    private String cropName;
    @Column(nullable = false, length = 100)
    private String fieldName;
    @Column(name = "reading_value", nullable = false, precision = 12, scale = 4)
    private BigDecimal value;
    @Column(nullable = false, length = 30)
    private String unit;
    @Column(nullable = false, length = 20)
    private String method;
    @Column(nullable = false, length = 30)
    private String accuracyClass;
    @Column(nullable = false)
    private LocalDate testedOn;
    @Column(nullable = false, length = 1000)
    private String notes;
    @Column(nullable = false, length = 500)
    private String interpretation;
    @Column(nullable = false)
    private Instant createdAt;

    public SoilResult() {
    }
    public Long getId() { return id; }
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
    public String getAccuracyClass() { return accuracyClass; }
    public void setAccuracyClass(String accuracyClass) { this.accuracyClass = accuracyClass; }
    public LocalDate getTestedOn() { return testedOn; }
    public void setTestedOn(LocalDate testedOn) { this.testedOn = testedOn; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public String getInterpretation() { return interpretation; }
    public void setInterpretation(String interpretation) { this.interpretation = interpretation; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
