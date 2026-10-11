package com.agri.ai.portal.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public class FarmTaskRequest {

    @NotBlank @Size(max = 160)
    private String title;
    @Size(max = 100)
    private String cropName;
    @NotBlank @Size(max = 50)
    private String taskType;
    @NotNull
    private LocalDate dueDate;

    public FarmTaskRequest() {
    }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getCropName() { return cropName; }
    public void setCropName(String cropName) { this.cropName = cropName; }
    public String getTaskType() { return taskType; }
    public void setTaskType(String taskType) { this.taskType = taskType; }
    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }
}
