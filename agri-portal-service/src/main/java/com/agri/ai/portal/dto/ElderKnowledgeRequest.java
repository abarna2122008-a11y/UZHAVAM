package com.agri.ai.portal.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ElderKnowledgeRequest {
    @NotBlank @Size(max=160) private String title;
    @NotBlank @Size(max=100) private String cropTag;
    @NotBlank @Size(max=100) private String topicTag;
    @NotBlank @Size(max=80) private String language;
    @NotBlank @Size(max=120) private String contributor;
    @NotBlank @Size(max=5000) private String noteText;

    public ElderKnowledgeRequest() {}
    public String getTitle() { return title; }
    public void setTitle(String value) { title=value; }
    public String getCropTag() { return cropTag; }
    public void setCropTag(String value) { cropTag=value; }
    public String getTopicTag() { return topicTag; }
    public void setTopicTag(String value) { topicTag=value; }
    public String getLanguage() { return language; }
    public void setLanguage(String value) { language=value; }
    public String getContributor() { return contributor; }
    public void setContributor(String value) { contributor=value; }
    public String getNoteText() { return noteText; }
    public void setNoteText(String value) { noteText=value; }
}
