package com.agri.ai.portal.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "elder_knowledge_notes")
public class ElderKnowledgeNote {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 160)
    private String title;
    @Column(nullable = false, length = 100)
    private String cropTag;
    @Column(nullable = false, length = 100)
    private String topicTag;
    @Column(nullable = false, length = 80)
    private String language;
    @Column(nullable = false, length = 120)
    private String contributor;
    @Column(nullable = false, columnDefinition = "text")
    private String noteText;
    @Column(nullable = false)
    private boolean verified;
    @Column(nullable = false)
    private Instant createdAt;

    public ElderKnowledgeNote() {
    }
    public Long getId() { return id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getCropTag() { return cropTag; }
    public void setCropTag(String cropTag) { this.cropTag = cropTag; }
    public String getTopicTag() { return topicTag; }
    public void setTopicTag(String topicTag) { this.topicTag = topicTag; }
    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }
    public String getContributor() { return contributor; }
    public void setContributor(String contributor) { this.contributor = contributor; }
    public String getNoteText() { return noteText; }
    public void setNoteText(String noteText) { this.noteText = noteText; }
    public boolean isVerified() { return verified; }
    public void setVerified(boolean verified) { this.verified = verified; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
