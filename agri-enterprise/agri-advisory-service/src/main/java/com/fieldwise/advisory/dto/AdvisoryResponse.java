package com.fieldwise.advisory.dto;

import java.util.ArrayList;
import java.util.List;

public class AdvisoryResponse {

    private String detectedLanguage;
    private String category;
    private String answer;
    private List<String> sources = new ArrayList<>();

    public AdvisoryResponse() {
    }

    public AdvisoryResponse(String detectedLanguage, String category, String answer, List<String> sources) {
        this.detectedLanguage = detectedLanguage;
        this.category = category;
        this.answer = answer;
        this.sources = new ArrayList<>(sources);
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

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public List<String> getSources() {
        return new ArrayList<>(sources);
    }

    public void setSources(List<String> sources) {
        this.sources = new ArrayList<>(sources);
    }
}
