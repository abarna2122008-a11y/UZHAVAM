package com.fieldwise.farmer.dto;

public class SchemeInfo {

    private String name;
    private String description;
    private String eligibilityNote;
    private String officialUrl;

    public SchemeInfo() {
    }

    public SchemeInfo(String name, String description, String eligibilityNote, String officialUrl) {
        this.name = name;
        this.description = description;
        this.eligibilityNote = eligibilityNote;
        this.officialUrl = officialUrl;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getEligibilityNote() { return eligibilityNote; }
    public void setEligibilityNote(String eligibilityNote) { this.eligibilityNote = eligibilityNote; }
    public String getOfficialUrl() { return officialUrl; }
    public void setOfficialUrl(String officialUrl) { this.officialUrl = officialUrl; }
}
