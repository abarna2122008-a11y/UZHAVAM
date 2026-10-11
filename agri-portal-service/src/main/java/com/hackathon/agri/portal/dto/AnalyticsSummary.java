package com.hackathon.agri.portal.dto;

import java.util.List;

public class AnalyticsSummary {

    private final long totalQueries;
    private final int categoryCount;
    private final int languageCount;
    private final int districtCount;
    private final List<AnalyticsItem> categories;
    private final List<AnalyticsItem> languages;
    private final List<AnalyticsItem> districts;

    public AnalyticsSummary(
            long totalQueries,
            int categoryCount,
            int languageCount,
            int districtCount,
            List<AnalyticsItem> categories,
            List<AnalyticsItem> languages,
            List<AnalyticsItem> districts) {
        this.totalQueries = totalQueries;
        this.categoryCount = categoryCount;
        this.languageCount = languageCount;
        this.districtCount = districtCount;
        this.categories = categories;
        this.languages = languages;
        this.districts = districts;
    }

    public long getTotalQueries() {
        return totalQueries;
    }

    public int getCategoryCount() {
        return categoryCount;
    }

    public int getLanguageCount() {
        return languageCount;
    }

    public int getDistrictCount() {
        return districtCount;
    }

    public List<AnalyticsItem> getCategories() {
        return categories;
    }

    public List<AnalyticsItem> getLanguages() {
        return languages;
    }

    public List<AnalyticsItem> getDistricts() {
        return districts;
    }
}
