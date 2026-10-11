package com.agri.ai.portal.repository;

import com.agri.ai.portal.model.FarmTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FarmTransactionRepository extends JpaRepository<FarmTransaction, Long> {
    List<FarmTransaction> findTop30ByOrderByEntryDateDesc();
}
