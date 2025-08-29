package com.skylink.controller;

import com.skylink.dto.PlanRequest;
import com.skylink.dto.PlanResponse;
import com.skylink.service.PlanService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/plans")
@CrossOrigin(origins = "*")
public class PlanController {

    @Autowired
    private PlanService planService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PlanResponse> createPlan(@Valid @RequestBody PlanRequest request) {
        PlanResponse plan = planService.createPlan(request);
        return new ResponseEntity<>(plan, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<PlanResponse>> getAllPlans() {
        List<PlanResponse> plans = planService.getAllPlans();
        return ResponseEntity.ok(plans);
    }

    @GetMapping("/active")
    public ResponseEntity<List<PlanResponse>> getActivePlans() {
        List<PlanResponse> plans = planService.getActivePlans();
        return ResponseEntity.ok(plans);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlanResponse> getPlanById(@PathVariable Long id) {
        PlanResponse plan = planService.getPlanById(id);
        return ResponseEntity.ok(plan);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PlanResponse> updatePlan(@PathVariable Long id, 
                                                  @Valid @RequestBody PlanRequest request) {
        PlanResponse plan = planService.updatePlan(id, request);
        return ResponseEntity.ok(plan);
    }

    @PutMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deactivatePlan(@PathVariable Long id) {
        planService.deactivatePlan(id);
        return ResponseEntity.ok("Plan deactivated successfully");
    }

    @PutMapping("/{id}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> activatePlan(@PathVariable Long id) {
        planService.activatePlan(id);
        return ResponseEntity.ok("Plan activated successfully");
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deletePlan(@PathVariable Long id) {
        planService.deletePlan(id);
        return ResponseEntity.ok("Plan deleted successfully");
    }
}