package com.agri.ai.portal.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;

import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "field_proof_records")
public class FieldProofRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 100)
    private String cropName;
    @Column(nullable = false)
    private LocalDate incidentDate;
    @Column(nullable = false, length = 120)
    private String location;
    @Column(nullable = false, length = 2000)
    private String incidentDetails;
    @Column(nullable = false, length = 160)
    private String photoName;
    @Column(nullable = false, length = 40)
    private String photoContentType;
    @Lob
    @JsonIgnore
    @Column(nullable = false)
    private byte[] photoData;
    @Column(nullable = false)
    private Instant createdAt;

    public FieldProofRecord() {
    }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCropName() { return cropName; }
    public void setCropName(String cropName) { this.cropName = cropName; }
    public LocalDate getIncidentDate() { return incidentDate; }
    public void setIncidentDate(LocalDate incidentDate) { this.incidentDate = incidentDate; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public String getIncidentDetails() { return incidentDetails; }
    public void setIncidentDetails(String incidentDetails) { this.incidentDetails = incidentDetails; }
    public String getPhotoName() { return photoName; }
    public void setPhotoName(String photoName) { this.photoName = photoName; }
    public String getPhotoContentType() { return photoContentType; }
    public void setPhotoContentType(String photoContentType) { this.photoContentType = photoContentType; }
    public byte[] getPhotoData() { return photoData; }
    public void setPhotoData(byte[] photoData) { this.photoData = photoData; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
