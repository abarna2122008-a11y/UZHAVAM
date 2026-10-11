package com.agri.ai.portal.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public class PestObservationRequest {
    @NotBlank @Size(max=100) private String cropName;
    @NotBlank @Size(max=100) private String symptom;
    @NotBlank @Size(max=100) private String district;
    @NotNull private LocalDate observedOn;
    @NotBlank @Size(max=2000) private String observations;
    @Size(max=1000) private String actionTaken;
    private boolean resolved;

    public PestObservationRequest() {}
    public String getCropName() { return cropName; }
    public void setCropName(String value) { cropName=value; }
    public String getSymptom() { return symptom; }
    public void setSymptom(String value) { symptom=value; }
    public String getDistrict() { return district; }
    public void setDistrict(String value) { district=value; }
    public LocalDate getObservedOn() { return observedOn; }
    public void setObservedOn(LocalDate value) { observedOn=value; }
    public String getObservations() { return observations; }
    public void setObservations(String value) { observations=value; }
    public String getActionTaken() { return actionTaken; }
    public void setActionTaken(String value) { actionTaken=value; }
    public boolean isResolved() { return resolved; }
    public void setResolved(boolean value) { resolved=value; }
}
