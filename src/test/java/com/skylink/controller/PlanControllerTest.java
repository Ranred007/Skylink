package com.skylink.controller;

import com.skylink.dto.PlanRequest;
import com.skylink.dto.PlanResponse;
import com.skylink.service.PlanService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PlanControllerTest {

    @Mock
    private PlanService planService;

    @InjectMocks
    private PlanController planController;

    private PlanResponse planResponse;
    private PlanRequest planRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // ✅ PlanResponse setup with setters
        planResponse = new PlanResponse();
        planResponse.setId(1L);
        planResponse.setName("Basic Plan");
        planResponse.setDescription("Basic Internet Package");
        planResponse.setPrice(BigDecimal.valueOf(499.99));
        planResponse.setDurationInDays(30);
        planResponse.setDataLimitGB(100);
        planResponse.setSpeedMbps(50);
        planResponse.setActive(true);

        // ✅ PlanRequest using constructor
        planRequest = new PlanRequest(
                "Basic Plan",
                "Basic Internet Package",
                BigDecimal.valueOf(499.99),
                30,
                100,
                50
        );
    }

    @Test
    void testCreatePlan() {
        when(planService.createPlan(any(PlanRequest.class))).thenReturn(planResponse);

        ResponseEntity<PlanResponse> response = planController.createPlan(planRequest);

        assertNotNull(response.getBody());
        assertEquals("Basic Plan", response.getBody().getName());
        assertEquals(499.99, response.getBody().getPrice().doubleValue());

        verify(planService, times(1)).createPlan(any(PlanRequest.class));
    }

    @Test
    void testGetPlanById() {
        when(planService.getPlanById(1L)).thenReturn(planResponse);

        ResponseEntity<PlanResponse> response = planController.getPlanById(1L);

        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
        assertEquals("Basic Plan", response.getBody().getName());

        verify(planService, times(1)).getPlanById(1L);
    }

    @Test
    void testGetAllPlans() {
        when(planService.getAllPlans()).thenReturn(Arrays.asList(planResponse));

        ResponseEntity<List<PlanResponse>> response = planController.getAllPlans();

        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals("Basic Plan", response.getBody().get(0).getName());

        verify(planService, times(1)).getAllPlans();
    }

    @Test
    void testDeletePlan() {
        doNothing().when(planService).deletePlan(1L);

        ResponseEntity<String> response = planController.deletePlan(1L);

        assertNotEquals(204, response.getStatusCodeValue()); // No Content
        verify(planService, times(1)).deletePlan(1L);
    }
}
