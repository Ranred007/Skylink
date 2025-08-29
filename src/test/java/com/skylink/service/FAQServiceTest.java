package com.skylink.service;

import com.skylink.dao.FAQRepository;
import com.skylink.dto.FAQRequest;
import com.skylink.dto.FAQResponse;
import com.skylink.entity.FAQ;
import com.skylink.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FAQServiceTest {

    @Mock
    private FAQRepository faqRepository;

    @InjectMocks
    private FAQService faqService;

    private FAQ faq;
    private FAQRequest faqRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        faq = new FAQ();
        faq.setId(1L);
        faq.setQuestion("What is Skylink?");
        faq.setAnswer("A telecom company.");
        faq.setCategory("General");
        faq.setDisplayOrder(1);
        faq.setActive(true);
        faq.setCreatedAt(LocalDateTime.now());
        faq.setUpdatedAt(LocalDateTime.now());

        faqRequest = new FAQRequest();
        faqRequest.setQuestion("Updated Question?");
        faqRequest.setAnswer("Updated Answer.");
        faqRequest.setCategory("General");
        faqRequest.setDisplayOrder(2);
    }

    @Test
    void createFAQ_ShouldReturnSavedFAQResponse() {
        when(faqRepository.save(any(FAQ.class))).thenReturn(faq);

        FAQResponse response = faqService.createFAQ(faqRequest);

        assertNotNull(response);
        assertEquals(faq.getQuestion(), response.getQuestion());
        verify(faqRepository, times(1)).save(any(FAQ.class));
    }

    @Test
    void getAllFAQs_ShouldReturnListOfFAQResponses() {
        when(faqRepository.findAll()).thenReturn(Arrays.asList(faq));

        List<FAQResponse> responses = faqService.getAllFAQs();

        assertEquals(1, responses.size());
        assertEquals("What is Skylink?", responses.get(0).getQuestion());
    }

    @Test
    void getActiveFAQs_ShouldReturnOnlyActiveFAQs() {
        when(faqRepository.findByActiveTrueOrderByDisplayOrderAsc()).thenReturn(Arrays.asList(faq));

        List<FAQResponse> responses = faqService.getActiveFAQs();

        assertEquals(1, responses.size());
        assertTrue(responses.get(0).getActive());
    }

    @Test
    void getFAQsByCategory_ShouldReturnFAQsOfCategory() {
        when(faqRepository.findActiveFAQsByCategoryOrderByDisplayOrder("General")).thenReturn(Arrays.asList(faq));

        List<FAQResponse> responses = faqService.getFAQsByCategory("General");

        assertEquals(1, responses.size());
        assertEquals("General", responses.get(0).getCategory());
    }

    @Test
    void getAllCategories_ShouldReturnListOfCategories() {
        when(faqRepository.findDistinctCategories()).thenReturn(Arrays.asList("General", "Billing"));

        List<String> categories = faqService.getAllCategories();

        assertEquals(2, categories.size());
        assertTrue(categories.contains("Billing"));
    }

    @Test
    void getFAQById_ShouldReturnFAQ_WhenExists() {
        when(faqRepository.findById(1L)).thenReturn(Optional.of(faq));

        FAQResponse response = faqService.getFAQById(1L);

        assertNotNull(response);
        assertEquals("What is Skylink?", response.getQuestion());
    }

    @Test
    void getFAQById_ShouldThrowException_WhenNotFound() {
        when(faqRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> faqService.getFAQById(99L));
    }

    @Test
    void updateFAQ_ShouldUpdateAndReturnResponse() {
        when(faqRepository.findById(1L)).thenReturn(Optional.of(faq));
        when(faqRepository.save(any(FAQ.class))).thenReturn(faq);

        FAQResponse response = faqService.updateFAQ(1L, faqRequest);

        assertNotNull(response);
        assertEquals("Updated Question?", response.getQuestion());
    }

    @Test
    void deactivateFAQ_ShouldSetActiveToFalse() {
        when(faqRepository.findById(1L)).thenReturn(Optional.of(faq));

        faqService.deactivateFAQ(1L);

        assertFalse(faq.getActive());
        verify(faqRepository, times(1)).save(faq);
    }

    @Test
    void activateFAQ_ShouldSetActiveToTrue() {
        faq.setActive(false);
        when(faqRepository.findById(1L)).thenReturn(Optional.of(faq));

        faqService.activateFAQ(1L);

        assertTrue(faq.getActive());
        verify(faqRepository, times(1)).save(faq);
    }

    @Test
    void deleteFAQ_ShouldDeleteFAQ() {
        when(faqRepository.findById(1L)).thenReturn(Optional.of(faq));

        faqService.deleteFAQ(1L);

        verify(faqRepository, times(1)).delete(faq);
    }

    @Test
    void searchFAQs_ShouldReturnMatchingFAQs() {
        when(faqRepository.searchByKeyword("Skylink")).thenReturn(Arrays.asList(faq));

        List<FAQResponse> responses = faqService.searchFAQs("Skylink");

        assertEquals(1, responses.size());
        assertEquals("What is Skylink?", responses.get(0).getQuestion());
    }
}
