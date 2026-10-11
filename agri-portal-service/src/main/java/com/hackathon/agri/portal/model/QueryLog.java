package com.hackathon.agri.portal.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "query_logs")
public class QueryLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 1000)
    private String userQuery;

    @Column(nullable = false, length = 120)
    private String district;

    @Column(nullable = false, length = 30)
    private String detectedLanguage;

    @Column(nullable = false, length = 80)
    private String category;

    @Column(nullable = false, length = 4000)
    private String aiResponse;

    @Column(nullable = false)
    private LocalDateTime createdTime;

    public QueryLog() {
    }

    public QueryLog(String userQuery, String district, String detectedLanguage, String category, String aiResponse) {
        this.userQuery = userQuery;
        this.district = district;
        this.detectedLanguage = detectedLanguage;
        this.category = category;
        this.aiResponse = aiResponse;
    }

    @PrePersist
    public void setCreatedTimeIfMissing() {
        if (createdTime == null) {
            createdTime = LocalDateTime.now();
        }
    }

    public Long getId() {
        return id;
    }

    public String getUserQuery() {
        return userQuery;
    }

    public void setUserQuery(String userQuery) {
        this.userQuery = userQuery;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getDetectedLanguage() {
        return detectedLanguage;
    }

    public void setDetectedLanguage(String detectedLanguage) {
        this.detectedLanguage = detectedLanguage;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getAiResponse() {
        return aiResponse;
    }

    public void setAiResponse(String aiResponse) {
        this.aiResponse = aiResponse;
    }

    public LocalDateTime getCreatedTime() {
        return createdTime;
    }
}
