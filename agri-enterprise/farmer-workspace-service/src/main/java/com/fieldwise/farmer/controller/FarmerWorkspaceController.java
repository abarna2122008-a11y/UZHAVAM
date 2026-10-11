package com.fieldwise.farmer.controller;

import com.fieldwise.farmer.dto.CropPlanRequest;
import com.fieldwise.farmer.dto.FarmTaskRequest;
import com.fieldwise.farmer.dto.FarmerProfileRequest;
import com.fieldwise.farmer.dto.MarketLink;
import com.fieldwise.farmer.dto.SchemeInfo;
import com.fieldwise.farmer.dto.WeatherSummary;
import com.fieldwise.farmer.model.CropPlan;
import com.fieldwise.farmer.model.FarmTask;
import com.fieldwise.farmer.model.FarmerProfile;
import com.fieldwise.farmer.service.FarmerWorkspaceService;
import com.fieldwise.farmer.service.WeatherService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Validated
@RestController
@RequestMapping("/api/farmers")
public class FarmerWorkspaceController {

    private final FarmerWorkspaceService workspaceService;
    private final WeatherService weatherService;

    public FarmerWorkspaceController(FarmerWorkspaceService workspaceService, WeatherService weatherService) {
        this.workspaceService = workspaceService;
        this.weatherService = weatherService;
    }

    @GetMapping("/profile")
    public FarmerProfile profile() {
        return workspaceService.getProfile();
    }

    @PutMapping("/profile")
    public FarmerProfile saveProfile(@Valid @RequestBody FarmerProfileRequest request) {
        return workspaceService.saveProfile(request);
    }

    @GetMapping("/crops")
    public List<CropPlan> cropPlans() {
        return workspaceService.getCropPlans();
    }

    @PostMapping("/crops")
    @ResponseStatus(HttpStatus.CREATED)
    public CropPlan addCropPlan(@Valid @RequestBody CropPlanRequest request) {
        try {
            return workspaceService.addCropPlan(request);
        } catch (IllegalArgumentException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, exception.getMessage());
        }
    }

    @DeleteMapping("/crops/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCropPlan(@PathVariable long id) {
        try {
            workspaceService.deleteCropPlan(id);
        } catch (FarmerWorkspaceService.ResourceNotFoundException exception) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, exception.getMessage());
        }
    }

    @GetMapping("/tasks")
    public List<FarmTask> tasks() {
        return workspaceService.getTasks();
    }

    @PostMapping("/tasks")
    @ResponseStatus(HttpStatus.CREATED)
    public FarmTask addTask(@Valid @RequestBody FarmTaskRequest request) {
        return workspaceService.addTask(request);
    }

    @PatchMapping("/tasks/{id}")
    public FarmTask completeTask(@PathVariable long id, @RequestParam boolean completed) {
        try {
            return workspaceService.completeTask(id, completed);
        } catch (FarmerWorkspaceService.ResourceNotFoundException exception) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, exception.getMessage());
        }
    }

    @GetMapping("/alerts")
    public List<FarmTask> alerts() {
        return workspaceService.getAlerts();
    }

    @GetMapping("/schemes")
    public List<SchemeInfo> schemes() {
        return workspaceService.getSchemes();
    }

    @GetMapping("/markets")
    public List<MarketLink> markets() {
        return workspaceService.getMarketLinks();
    }

    @GetMapping("/weather")
    public WeatherSummary weather(@RequestParam @NotBlank String district) {
        return weatherService.getForecast(district);
    }

    @GetMapping("/context")
    public FarmerWorkspaceService.FarmContextSummary context(@RequestParam @NotBlank String location) {
        return workspaceService.getContext(location);
    }
}
