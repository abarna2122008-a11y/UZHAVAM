package com.hackathon.agri.advisory.dto;

public class AdvisoryRequest {

    private String query;
    private String district;
    private String preferredLanguage;

    public AdvisoryRequest() {
    }

    public AdvisoryRequest(String query, String district) {
        this(query, district, "auto");
    }

    public AdvisoryRequest(String query, String district, String preferredLanguage) {
        this.query = query;
        this.district = district;
        this.preferredLanguage = preferredLanguage;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getPreferredLanguage() {
        return preferredLanguage;
    }

    public void setPreferredLanguage(String preferredLanguage) {
        this.preferredLanguage = preferredLanguage;
    }
}
