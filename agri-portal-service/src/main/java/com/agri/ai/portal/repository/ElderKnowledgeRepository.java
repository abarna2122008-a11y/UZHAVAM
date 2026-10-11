package com.agri.ai.portal.repository;

import com.agri.ai.portal.model.ElderKnowledgeNote;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ElderKnowledgeRepository extends JpaRepository<ElderKnowledgeNote, Long> {
    List<ElderKnowledgeNote> findAllByOrderByCreatedAtDesc();
}
