package com.agri.ai.portal.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public class WeatherAlertMessage {
    @NotBlank @Size(max = 200)
    private String alertKey;
    @NotBlank @Size(max = 120)
    private String district;
    @NotBlank @Size(max = 500)
    private String headline;
    @NotBlank @Size(max = 2000)
    private String riskDetails;
    @NotNull
    private LocalDate forecastDate;
    @NotBlank @Size(max = 80)
    private String source;

    public WeatherAlertMessage() {
    }
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
}
