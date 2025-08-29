package com.skylink.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.skylink.dao.PlanRepository;
import com.skylink.dto.PlanRequest;
import com.skylink.dto.PlanResponse;
import com.skylink.entity.Plan;
import com.skylink.exception.ResourceNotFoundException;

@ExtendWith(MockitoExtension.class)
class PlanServiceTest {

    @Mock
    private PlanRepository planRepository;

    @InjectMocks
    private PlanService planService;

    private Plan activePlan;
    private Plan inactivePlan;
    private PlanRequest planRequest;

    @BeforeEach
    void setUp() {
        // Create active plan
        activePlan = new Plan();
        activePlan.setId(1L);
        activePlan.setName("Basic Plan");
        activePlan.setDescription("Basic internet plan");
        activePlan.setPrice(BigDecimal.valueOf(100));
        activePlan.setDurationInDays(30);
        activePlan.setDataLimitGB(100);
        activePlan.setSpeedMbps(50);
        activePlan.setActive(true);
        activePlan.setCreatedAt(LocalDateTime.now().minusDays(1));
        activePlan.setUpdatedAt(LocalDateTime.now().minusDays(1));

        // Create inactive plan
        inactivePlan = new Plan();
        inactivePlan.setId(2L);
        inactivePlan.setName("Premium Plan");
        inactivePlan.setDescription("Premium internet plan");
        inactivePlan.setPrice(BigDecimal.valueOf(200));
        inactivePlan.setDurationInDays(30);
        inactivePlan.setDataLimitGB(500);
        inactivePlan.setSpeedMbps(100);
        inactivePlan.setActive(false);
        inactivePlan.setCreatedAt(LocalDateTime.now().minusDays(2));
        inactivePlan.setUpdatedAt(LocalDateTime.now().minusDays(2));

        // Create plan request
        planRequest = new PlanRequest();
        planRequest.setName("Standard Plan");
        planRequest.setDescription("Standard internet plan");
        planRequest.setPrice(BigDecimal.valueOf(150));
        planRequest.setDurationInDays(30);
        planRequest.setDataLimitGB(250);
        planRequest.setSpeedMbps(75);
    }

    @Test
    void testCreatePlan_Success() {
        Plan newPlan = new Plan();
        newPlan.setId(3L);
        newPlan.setName(planRequest.getName());
        newPlan.setDescription(planRequest.getDescription());
        newPlan.setPrice(planRequest.getPrice());
        newPlan.setDurationInDays(planRequest.getDurationInDays());
        newPlan.setDataLimitGB(planRequest.getDataLimitGB());
        newPlan.setSpeedMbps(planRequest.getSpeedMbps());
        newPlan.setActive(true);

        when(planRepository.save(any(Plan.class))).thenReturn(newPlan);

        PlanResponse response = planService.createPlan(planRequest);

        assertThat(response).isNotNull();
        assertThat(response.getName()).isEqualTo("Standard Plan");
        assertThat(response.getDescription()).isEqualTo("Standard internet plan");
        assertThat(response.getPrice()).isEqualTo(BigDecimal.valueOf(150));
        assertThat(response.getDurationInDays()).isEqualTo(30);
        assertThat(response.getDataLimitGB()).isEqualTo(250);
        assertThat(response.getSpeedMbps()).isEqualTo(75);
        assertThat(response.getActive()).isTrue();

        verify(planRepository).save(any(Plan.class));
    }

    @Test
    void testGetAllPlans() {
        when(planRepository.findAll()).thenReturn(List.of(activePlan, inactivePlan));

        List<PlanResponse> responses = planService.getAllPlans();

        assertThat(responses).hasSize(2);
        assertThat(responses).extracting(PlanResponse::getName)
            .contains("Basic Plan", "Premium Plan");
        verify(planRepository).findAll();
    }

    @Test
    void testGetActivePlans() {
        when(planRepository.findByActiveTrueOrderByPriceAsc()).thenReturn(List.of(activePlan));

        List<PlanResponse> responses = planService.getActivePlans();

        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getName()).isEqualTo("Basic Plan");
        assertThat(responses.get(0).getActive()).isTrue();
        verify(planRepository).findByActiveTrueOrderByPriceAsc();
    }

    @Test
    void testGetPlanById_Success() {
        when(planRepository.findById(1L)).thenReturn(Optional.of(activePlan));

        PlanResponse response = planService.getPlanById(1L);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getName()).isEqualTo("Basic Plan");
        verify(planRepository).findById(1L);
    }

    @Test
    void testGetPlanById_NotFound() {
        when(planRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            planService.getPlanById(999L);
        });

        verify(planRepository).findById(999L);
    }

    @Test
    void testUpdatePlan_Success() {
        when(planRepository.findById(1L)).thenReturn(Optional.of(activePlan));
        
        Plan updatedPlan = new Plan();
        updatedPlan.setId(1L);
        updatedPlan.setName(planRequest.getName());
        updatedPlan.setDescription(planRequest.getDescription());
        updatedPlan.setPrice(planRequest.getPrice());
        updatedPlan.setDurationInDays(planRequest.getDurationInDays());
        updatedPlan.setDataLimitGB(planRequest.getDataLimitGB());
        updatedPlan.setSpeedMbps(planRequest.getSpeedMbps());
        updatedPlan.setActive(true);
        updatedPlan.setCreatedAt(activePlan.getCreatedAt());
        updatedPlan.setUpdatedAt(LocalDateTime.now());

        when(planRepository.save(any(Plan.class))).thenReturn(updatedPlan);

        PlanResponse response = planService.updatePlan(1L, planRequest);

        assertThat(response).isNotNull();
        assertThat(response.getName()).isEqualTo("Standard Plan");
        assertThat(response.getDescription()).isEqualTo("Standard internet plan");
        assertThat(response.getPrice()).isEqualTo(BigDecimal.valueOf(150));
        assertThat(response.getDurationInDays()).isEqualTo(30);
        assertThat(response.getDataLimitGB()).isEqualTo(250);
        assertThat(response.getSpeedMbps()).isEqualTo(75);

        verify(planRepository).findById(1L);
        verify(planRepository).save(any(Plan.class));
    }

    @Test
    void testUpdatePlan_NotFound() {
        when(planRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            planService.updatePlan(999L, planRequest);
        });

        verify(planRepository).findById(999L);
        verify(planRepository, never()).save(any(Plan.class));
    }

    @Test
    void testDeactivatePlan_Success() {
        when(planRepository.findById(1L)).thenReturn(Optional.of(activePlan));
        when(planRepository.save(any(Plan.class))).thenAnswer(invocation -> invocation.getArgument(0));

        planService.deactivatePlan(1L);

        verify(planRepository).findById(1L);
        verify(planRepository).save(any(Plan.class));
        assertThat(activePlan.getActive()).isFalse();
    }

    @Test
    void testDeactivatePlan_NotFound() {
        when(planRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            planService.deactivatePlan(999L);
        });

        verify(planRepository).findById(999L);
        verify(planRepository, never()).save(any(Plan.class));
    }

    @Test
    void testActivatePlan_Success() {
        when(planRepository.findById(2L)).thenReturn(Optional.of(inactivePlan));
        when(planRepository.save(any(Plan.class))).thenAnswer(invocation -> invocation.getArgument(0));

        planService.activatePlan(2L);

        verify(planRepository).findById(2L);
        verify(planRepository).save(any(Plan.class));
        assertThat(inactivePlan.getActive()).isTrue();
    }

    @Test
    void testActivatePlan_NotFound() {
        when(planRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            planService.activatePlan(999L);
        });

        verify(planRepository).findById(999L);
        verify(planRepository, never()).save(any(Plan.class));
    }

    @Test
    void testDeletePlan_Success() {
        when(planRepository.findById(1L)).thenReturn(Optional.of(activePlan));
        when(planRepository.countSubscriptionsByPlanId(1L)).thenReturn(0L);

        planService.deletePlan(1L);

        verify(planRepository).findById(1L);
        verify(planRepository).countSubscriptionsByPlanId(1L);
        verify(planRepository).delete(activePlan);
    }

    @Test
    void testDeletePlan_WithActiveSubscriptions() {
        when(planRepository.findById(1L)).thenReturn(Optional.of(activePlan));
        when(planRepository.countSubscriptionsByPlanId(1L)).thenReturn(5L);

        assertThrows(IllegalStateException.class, () -> {
            planService.deletePlan(1L);
        });

        verify(planRepository).findById(1L);
        verify(planRepository).countSubscriptionsByPlanId(1L);
        verify(planRepository, never()).delete(any(Plan.class));
    }

    @Test
    void testDeletePlan_NotFound() {
        when(planRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            planService.deletePlan(999L);
        });

        verify(planRepository).findById(999L);
        verify(planRepository, never()).countSubscriptionsByPlanId(any());
        verify(planRepository, never()).delete(any(Plan.class));
    }

    @Test
    void testConvertToPlanResponse() {
        PlanResponse response = planService.convertToPlanResponse(activePlan);

        assertThat(response.getId()).isEqualTo(activePlan.getId());
        assertThat(response.getName()).isEqualTo(activePlan.getName());
        assertThat(response.getDescription()).isEqualTo(activePlan.getDescription());
        assertThat(response.getPrice()).isEqualTo(activePlan.getPrice());
        assertThat(response.getDurationInDays()).isEqualTo(activePlan.getDurationInDays());
        assertThat(response.getDataLimitGB()).isEqualTo(activePlan.getDataLimitGB());
        assertThat(response.getSpeedMbps()).isEqualTo(activePlan.getSpeedMbps());
        assertThat(response.getActive()).isEqualTo(activePlan.getActive());
        assertThat(response.getCreatedAt()).isEqualTo(activePlan.getCreatedAt());
        assertThat(response.getUpdatedAt()).isEqualTo(activePlan.getUpdatedAt());
    }

    @Test
    void testGetActivePlans_EmptyList() {
        when(planRepository.findByActiveTrueOrderByPriceAsc()).thenReturn(List.of());

        List<PlanResponse> responses = planService.getActivePlans();

        assertThat(responses).isEmpty();
        verify(planRepository).findByActiveTrueOrderByPriceAsc();
    }

    @Test
    void testGetAllPlans_EmptyList() {
        when(planRepository.findAll()).thenReturn(List.of());

        List<PlanResponse> responses = planService.getAllPlans();

        assertThat(responses).isEmpty();
        verify(planRepository).findAll();
    }

    @Test
    void testUpdatePlan_PartialFields() {
        // Create a request with only some fields set (others are null)
        PlanRequest partialRequest = new PlanRequest();
        partialRequest.setName("Updated Name");
        partialRequest.setPrice(BigDecimal.valueOf(99));
        // Other fields remain null

        when(planRepository.findById(1L)).thenReturn(Optional.of(activePlan));
        
        // The service will update ALL fields, including setting null values for fields not in the request
        Plan updatedPlan = new Plan();
        updatedPlan.setId(1L);
        updatedPlan.setName("Updated Name");
        updatedPlan.setDescription(null); // This will be set to null by the service
        updatedPlan.setPrice(BigDecimal.valueOf(99));
        updatedPlan.setDurationInDays(null); // This will be set to null by the service
        updatedPlan.setDataLimitGB(null); // This will be set to null by the service
        updatedPlan.setSpeedMbps(null); // This will be set to null by the service
        updatedPlan.setActive(activePlan.getActive());
        updatedPlan.setCreatedAt(activePlan.getCreatedAt());
        updatedPlan.setUpdatedAt(LocalDateTime.now());

        when(planRepository.save(any(Plan.class))).thenReturn(updatedPlan);

        PlanResponse response = planService.updatePlan(1L, partialRequest);

        // With the current implementation, all fields get updated (even to null)
        assertThat(response.getName()).isEqualTo("Updated Name");
        assertThat(response.getPrice()).isEqualTo(BigDecimal.valueOf(99));
        assertThat(response.getDescription()).isNull(); // This will be null
        assertThat(response.getDurationInDays()).isNull(); // This will be null
        assertThat(response.getDataLimitGB()).isNull(); // This will be null
        assertThat(response.getSpeedMbps()).isNull(); // This will be null

        verify(planRepository).findById(1L);
        verify(planRepository).save(any(Plan.class));
    }
}