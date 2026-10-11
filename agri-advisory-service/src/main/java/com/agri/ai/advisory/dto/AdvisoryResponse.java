package com.agri.ai.advisory.dto;

import java.util.ArrayList;
import java.util.List;

public class AdvisoryResponse {

    private String query;
    private String language;
    private String category;
    private String response;
    private List<String> sources = new ArrayList<>();

    public AdvisoryResponse() {
    }

    public AdvisoryResponse(String query, String language, String category, String response, List<String> sources) {
        this.query = query;
        this.language = language;
        this.category = category;
        this.response = response;
        this.sources = new ArrayList<>(sources);
    }

    public String getQuery() { return query; }
    public void setQuery(String query) { this.query = query; }
    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getResponse() { return response; }
    public void setResponse(String response) { this.response = response; }
    public List<String> getSources() { return new ArrayList<>(sources); }
    public void setSources(List<String> sources) { this.sources = new ArrayList<>(sources); }
}
