package com.fieldwise.advisory.dto;

import java.util.ArrayList;
import java.util.List;

public class FarmContext {

    private String location;
    private String farmSummary;
    private String cropPlanSummary;
    private String upcomingTasks;
    private List<String> references = new ArrayList<>();

    public FarmContext() {
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getFarmSummary() {
        return farmSummary;
    }

    public void setFarmSummary(String farmSummary) {
        this.farmSummary = farmSummary;
    }

    public String getCropPlanSummary() {
        return cropPlanSummary;
    }

    public void setCropPlanSummary(String cropPlanSummary) {
        this.cropPlanSummary = cropPlanSummary;
    }

    public String getUpcomingTasks() {
        return upcomingTasks;
    }

    public void setUpcomingTasks(String upcomingTasks) {
        this.upcomingTasks = upcomingTasks;
    }

    public List<String> getReferences() {
        return new ArrayList<>(references);
    }

    public void setReferences(List<String> references) {
        this.references = new ArrayList<>(references);
    }
}
