package com.skylink.controller;

import com.skylink.dto.FAQRequest;
import com.skylink.dto.FAQResponse;
import com.skylink.service.FAQService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class FAQControllerTest {

    @Mock
    private FAQService faqService;

    @InjectMocks
    private FAQController faqController;

    private FAQResponse faqResponse;
    private FAQRequest faqRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // ✅ Setup FAQResponse with setters
        faqResponse = new FAQResponse();
        faqResponse.setId(1L);
        faqResponse.setQuestion("What is Skyline?");
        faqResponse.setAnswer("Skyline is a telecom service provider.");
        faqResponse.setCategory("General");
        faqResponse.setActive(true);

        // ✅ Setup FAQRequest
        faqRequest = new FAQRequest();
        faqRequest.setQuestion("What is Skyline?");
        faqRequest.setAnswer("Skyline is a telecom service provider.");
        faqRequest.setCategory("General");
    }

    @Test
    void testCreateFAQ() {
        when(faqService.createFAQ(any(FAQRequest.class))).thenReturn(faqResponse);

        ResponseEntity<FAQResponse> response = faqController.createFAQ(faqRequest);

        assertNotNull(response.getBody());
        assertEquals("What is Skyline?", response.getBody().getQuestion());
        verify(faqService, times(1)).createFAQ(any(FAQRequest.class));
    }

    @Test
    void testGetAllFAQs() {
        when(faqService.getAllFAQs()).thenReturn(Arrays.asList(faqResponse));

        ResponseEntity<List<FAQResponse>> response = faqController.getAllFAQs();

        assertEquals(1, response.getBody().size());
        assertEquals("General", response.getBody().get(0).getCategory());
        verify(faqService, times(1)).getAllFAQs();
    }

    @Test
    void testGetActiveFAQs() {
        when(faqService.getActiveFAQs()).thenReturn(Arrays.asList(faqResponse));

        ResponseEntity<List<FAQResponse>> response = faqController.getActiveFAQs();

        assertEquals(1, response.getBody().size());
        assertTrue(response.getBody().get(0).getActive());
        verify(faqService, times(1)).getActiveFAQs();
    }

    @Test
    void testGetFAQsByCategory() {
        when(faqService.getFAQsByCategory("General")).thenReturn(Arrays.asList(faqResponse));

        ResponseEntity<List<FAQResponse>> response = faqController.getFAQsByCategory("General");

        assertEquals(1, response.getBody().size());
        assertEquals("General", response.getBody().get(0).getCategory());
        verify(faqService, times(1)).getFAQsByCategory("General");
    }

    @Test
    void testGetAllCategories() {
        when(faqService.getAllCategories()).thenReturn(Arrays.asList("General", "Billing"));

        ResponseEntity<List<String>> response = faqController.getAllCategories();

        assertEquals(2, response.getBody().size());
        assertTrue(response.getBody().contains("Billing"));
        verify(faqService, times(1)).getAllCategories();
    }

    @Test
    void testGetFAQById() {
        when(faqService.getFAQById(1L)).thenReturn(faqResponse);

        ResponseEntity<FAQResponse> response = faqController.getFAQById(1L);

        assertEquals("What is Skyline?", response.getBody().getQuestion());
        verify(faqService, times(1)).getFAQById(1L);
    }

    @Test
    void testUpdateFAQ() {
        when(faqService.updateFAQ(eq(1L), any(FAQRequest.class))).thenReturn(faqResponse);

        ResponseEntity<FAQResponse> response = faqController.updateFAQ(1L, faqRequest);

        assertEquals("What is Skyline?", response.getBody().getQuestion());
        verify(faqService, times(1)).updateFAQ(eq(1L), any(FAQRequest.class));
    }

    @Test
    void testDeactivateFAQ() {
        doNothing().when(faqService).deactivateFAQ(1L);

        ResponseEntity<String> response = faqController.deactivateFAQ(1L);

        assertEquals("FAQ deactivated successfully", response.getBody());
        verify(faqService, times(1)).deactivateFAQ(1L);
    }

    @Test
    void testActivateFAQ() {
        doNothing().when(faqService).activateFAQ(1L);

        ResponseEntity<String> response = faqController.activateFAQ(1L);

        assertEquals("FAQ activated successfully", response.getBody());
        verify(faqService, times(1)).activateFAQ(1L);
    }

    @Test
    void testDeleteFAQ() {
        doNothing().when(faqService).deleteFAQ(1L);

        ResponseEntity<String> response = faqController.deleteFAQ(1L);

        assertEquals("FAQ deleted successfully", response.getBody());
        verify(faqService, times(1)).deleteFAQ(1L);
    }

    @Test
    void testSearchFAQs() {
        when(faqService.searchFAQs("Skyline")).thenReturn(Arrays.asList(faqResponse));

        ResponseEntity<List<FAQResponse>> response = faqController.searchFAQs("Skyline");

        assertEquals(1, response.getBody().size());
        assertEquals("Skyline is a telecom service provider.", response.getBody().get(0).getAnswer());
        verify(faqService, times(1)).searchFAQs("Skyline");
    }
}
