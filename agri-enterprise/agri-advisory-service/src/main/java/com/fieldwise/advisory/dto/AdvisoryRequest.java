package com.fieldwise.advisory.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class AdvisoryRequest {

    @NotBlank
    @Size(max = 2000)
    private String query;

    @NotBlank
    private String language;

    @NotBlank
    @Size(max = 120)
    private String location;

    public AdvisoryRequest() {
    }

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
