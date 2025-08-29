package com.skylink.service;

import com.skylink.dao.PlanRepository;
import com.skylink.dto.PlanRequest;
import com.skylink.dto.PlanResponse;
import com.skylink.entity.Plan;
import com.skylink.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class PlanService {

    @Autowired
    private PlanRepository planRepository;

    public PlanResponse createPlan(PlanRequest request) {
        Plan plan = new Plan();
        plan.setName(request.getName());
        plan.setDescription(request.getDescription());
        plan.setPrice(request.getPrice());
        plan.setDurationInDays(request.getDurationInDays());
        plan.setDataLimitGB(request.getDataLimitGB());
        plan.setSpeedMbps(request.getSpeedMbps());

        Plan savedPlan = planRepository.save(plan);
        return convertToPlanResponse(savedPlan);
    }

    public List<PlanResponse> getAllPlans() {
        return planRepository.findAll().stream()
                .map(this::convertToPlanResponse)
                .collect(Collectors.toList());
    }

    public List<PlanResponse> getActivePlans() {
        return planRepository.findByActiveTrueOrderByPriceAsc().stream()
                .map(this::convertToPlanResponse)
                .collect(Collectors.toList());
    }

    public PlanResponse getPlanById(Long id) {
        Plan plan = planRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Plan not found with id: " + id));
        return convertToPlanResponse(plan);
    }

    public PlanResponse updatePlan(Long id, PlanRequest request) {
        Plan plan = planRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Plan not found with id: " + id));

        plan.setName(request.getName());
        plan.setDescription(request.getDescription());
        plan.setPrice(request.getPrice());
        plan.setDurationInDays(request.getDurationInDays());
        plan.setDataLimitGB(request.getDataLimitGB());
        plan.setSpeedMbps(request.getSpeedMbps());

        Plan updatedPlan = planRepository.save(plan);
        return convertToPlanResponse(updatedPlan);
    }

    public void deactivatePlan(Long id) {
        Plan plan = planRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Plan not found with id: " + id));
        plan.setActive(false);
        planRepository.save(plan);
    }

    public void activatePlan(Long id) {
        Plan plan = planRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Plan not found with id: " + id));
        plan.setActive(true);
        planRepository.save(plan);
    }

    public void deletePlan(Long id) {
        Plan plan = planRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Plan not found with id: " + id));
        
        // Check if plan has active subscriptions
        long activeSubscriptions = planRepository.countSubscriptionsByPlanId(id);
        if (activeSubscriptions > 0) {
            throw new IllegalStateException("Cannot delete plan with active subscriptions");
        }
        
        planRepository.delete(plan);
    }

    PlanResponse convertToPlanResponse(Plan plan) {
        PlanResponse response = new PlanResponse();
        response.setId(plan.getId());
        response.setName(plan.getName());
        response.setDescription(plan.getDescription());
        response.setPrice(plan.getPrice());
        response.setDurationInDays(plan.getDurationInDays());
        response.setDataLimitGB(plan.getDataLimitGB());
        response.setSpeedMbps(plan.getSpeedMbps());
        response.setActive(plan.getActive());
        response.setCreatedAt(plan.getCreatedAt());
        response.setUpdatedAt(plan.getUpdatedAt());
        return response;
    }
}