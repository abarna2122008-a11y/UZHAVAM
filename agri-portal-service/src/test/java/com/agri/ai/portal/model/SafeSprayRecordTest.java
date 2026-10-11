package com.agri.ai.portal.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SafeSprayRecordTest {

    @Test
    void calculatesLabelBasedPreHarvestDateAndComparesPlannedHarvest() {
        SafeSprayRecord record = new SafeSprayRecord();
        record.setAppliedDate(LocalDate.of(2026, 10, 1));
        record.setPreHarvestIntervalDays(14);

        assertEquals(LocalDate.of(2026, 10, 15), record.getSafeHarvestDate());
        record.setPlannedHarvestDate(LocalDate.of(2026, 10, 14));
        assertFalse(record.isHarvestDateSafe());
        record.setPlannedHarvestDate(LocalDate.of(2026, 10, 15));
        assertTrue(record.isHarvestDateSafe());
    }
}
