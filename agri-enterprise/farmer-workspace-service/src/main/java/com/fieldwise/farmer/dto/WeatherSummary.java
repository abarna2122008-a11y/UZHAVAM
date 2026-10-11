package com.fieldwise.farmer.dto;

import java.util.ArrayList;
import java.util.List;

public class WeatherSummary {

    private String district;
    private boolean available;
    private String summary;
    private List<DailyForecast> forecast = new ArrayList<>();
    private String source;

    public WeatherSummary() {
    }

    public WeatherSummary(String district, boolean available, String summary, List<DailyForecast> forecast, String source) {
        this.district = district;
        this.available = available;
        this.summary = summary;
        this.forecast = new ArrayList<>(forecast);
        this.source = source;
    }

    public String getDistrict() { return district; }
    public void setDistrict(String district) { this.district = district; }
    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }
    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }
    public List<DailyForecast> getForecast() { return new ArrayList<>(forecast); }
    public void setForecast(List<DailyForecast> forecast) { this.forecast = new ArrayList<>(forecast); }
    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }

    public static class DailyForecast {
        private String date;
        private String maxTemperatureC;
        private String precipitationProbability;

        public DailyForecast() {
        }

        public DailyForecast(String date, String maxTemperatureC, String precipitationProbability) {
            this.date = date;
            this.maxTemperatureC = maxTemperatureC;
            this.precipitationProbability = precipitationProbability;
        }

        public String getDate() { return date; }
        public void setDate(String date) { this.date = date; }
        public String getMaxTemperatureC() { return maxTemperatureC; }
        public void setMaxTemperatureC(String maxTemperatureC) { this.maxTemperatureC = maxTemperatureC; }
        public String getPrecipitationProbability() { return precipitationProbability; }
        public void setPrecipitationProbability(String precipitationProbability) { this.precipitationProbability = precipitationProbability; }
    }
}
