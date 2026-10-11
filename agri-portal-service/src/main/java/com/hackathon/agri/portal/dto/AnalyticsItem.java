package com.hackathon.agri.portal.dto;

public class AnalyticsItem {

    private final String label;
    private final long count;
    private final int percentage;

    public AnalyticsItem(String label, long count, int percentage) {
        this.label = label;
        this.count = count;
        this.percentage = percentage;
    }

    public String getLabel() {
        return label;
    }

    public long getCount() {
        return count;
    }

    public int getPercentage() {
        return percentage;
    }
}
