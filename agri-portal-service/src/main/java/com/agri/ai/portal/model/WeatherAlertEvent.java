package com.agri.ai.portal.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "weather_alert_events")
public class WeatherAlertEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true, length = 200)
    private String alertKey;
    @Column(nullable = false, length = 120)
    private String district;
    @Column(nullable = false, length = 500)
    private String headline;
    @Column(nullable = false, length = 2000)
    private String riskDetails;
    @Column(nullable = false)
    private LocalDate forecastDate;
    @Column(nullable = false, length = 80)
    private String source;
    @Column(nullable = false)
    private Instant receivedAt;

    public WeatherAlertEvent() {
    }
    public Long getId() { return id; }
    public String getAlertKey() { return alertKey; }
    public void setAlertKey(String value) { alertKey=value; }
    public String getDistrict() { return district; }
    public void setDistrict(String value) { district=value; }
    public String getHeadline() { return headline; }
    public void setHeadline(String value) { headline=value; }
    public String getRiskDetails() { return riskDetails; }
    public void setRiskDetails(String value) { riskDetails=value; }
    public LocalDate getForecastDate() { return forecastDate; }
    public void setForecastDate(LocalDate value) { forecastDate=value; }
    public String getSource() { return source; }
    public void setSource(String value) { source=value; }
    public Instant getReceivedAt() { return receivedAt; }
    public void setReceivedAt(Instant value) { receivedAt=value; }
}
