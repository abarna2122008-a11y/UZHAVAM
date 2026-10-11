package com.agri.ai.advisory.dto;

import jakarta.validation.constraints.NotBlank;

public class AdvisoryRequest {
    @NotBlank
    private String query;
    @NotBlank
    private String language;
    private String location;

    public AdvisoryRequest() {}

    public AdvisoryRequest(String query, String language, String location) {
        this.query = query;
        this.language = language;
        this.location = location;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
