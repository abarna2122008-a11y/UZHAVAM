package com.fieldwise.advisory.service;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;

@Service
public class CropKnowledgeService {

    public List<String> retrieve(String query) {
        String normalized = query.toLowerCase(Locale.ROOT);
        if (normalized.contains("rice") || normalized.contains("paddy")
                || normalized.contains("நெல்") || normalized.contains("धान")) {
            return List.of(
                    "[CROP-RICE-01] Rice: use a locally recommended variety and planting window; manage field water by crop stage and base fertilizer on soil testing.",
                    "[CROP-IPM-01] For suspected pest or disease, identify the cause and monitor crop damage before treatment; confirm any product with a local agriculture officer.");
        }
        if (normalized.contains("cotton") || normalized.contains("cotton")) {
            return List.of(
                    "[CROP-COTTON-01] Cotton: scout fields regularly, conserve beneficial insects, and follow locally approved integrated pest management recommendations.",
                    "[CROP-IPM-01] Identify a pest and estimate crop damage before considering treatment; follow local label and extension guidance.");
        }
        return List.of(
                "[CROP-GENERAL-01] Crop choice depends on soil, season, water availability, and local markets. A location name alone is not enough to recommend a crop.",
                "[CROP-IPM-01] For suspected pest or disease, identify the cause and monitor crop damage before treatment; confirm any product with a local agriculture officer.");
    }
}
