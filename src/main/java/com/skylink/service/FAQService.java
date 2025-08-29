package com.skylink.service;

import com.skylink.dao.FAQRepository;
import com.skylink.dto.FAQRequest;
import com.skylink.dto.FAQResponse;
import com.skylink.entity.FAQ;
import com.skylink.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class FAQService {

    @Autowired
    private FAQRepository faqRepository;

    public FAQResponse createFAQ(FAQRequest request) {
        FAQ faq = new FAQ();
        faq.setQuestion(request.getQuestion());
        faq.setAnswer(request.getAnswer());
        faq.setCategory(request.getCategory());
        faq.setDisplayOrder(request.getDisplayOrder() != null ? request.getDisplayOrder() : 0);

        FAQ savedFAQ = faqRepository.save(faq);
        return convertToFAQResponse(savedFAQ);
    }

    public List<FAQResponse> getAllFAQs() {
        return faqRepository.findAll().stream()
                .map(this::convertToFAQResponse)
                .collect(Collectors.toList());
    }

    public List<FAQResponse> getActiveFAQs() {
        return faqRepository.findByActiveTrueOrderByDisplayOrderAsc().stream()
                .map(this::convertToFAQResponse)
                .collect(Collectors.toList());
    }

    public List<FAQResponse> getFAQsByCategory(String category) {
        return faqRepository.findActiveFAQsByCategoryOrderByDisplayOrder(category).stream()
                .map(this::convertToFAQResponse)
                .collect(Collectors.toList());
    }

    public List<String> getAllCategories() {
        return faqRepository.findDistinctCategories();
    }

    public FAQResponse getFAQById(Long id) {
        FAQ faq = faqRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("FAQ not found with id: " + id));
        return convertToFAQResponse(faq);
    }

    public FAQResponse updateFAQ(Long id, FAQRequest request) {
        FAQ faq = faqRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("FAQ not found with id: " + id));

        faq.setQuestion(request.getQuestion());
        faq.setAnswer(request.getAnswer());
        faq.setCategory(request.getCategory());
        faq.setDisplayOrder(request.getDisplayOrder() != null ? request.getDisplayOrder() : faq.getDisplayOrder());

        FAQ updatedFAQ = faqRepository.save(faq);
        return convertToFAQResponse(updatedFAQ);
    }

    public void deactivateFAQ(Long id) {
        FAQ faq = faqRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("FAQ not found with id: " + id));
        faq.setActive(false);
        faqRepository.save(faq);
    }

    public void activateFAQ(Long id) {
        FAQ faq = faqRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("FAQ not found with id: " + id));
        faq.setActive(true);
        faqRepository.save(faq);
    }

    public void deleteFAQ(Long id) {
        FAQ faq = faqRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("FAQ not found with id: " + id));
        faqRepository.delete(faq);
    }

    public List<FAQResponse> searchFAQs(String keyword) {
        return faqRepository.searchByKeyword(keyword).stream()
                .map(this::convertToFAQResponse)
                .collect(Collectors.toList());
    }

    private FAQResponse convertToFAQResponse(FAQ faq) {
        FAQResponse response = new FAQResponse();
        response.setId(faq.getId());
        response.setQuestion(faq.getQuestion());
        response.setAnswer(faq.getAnswer());
        response.setCategory(faq.getCategory());
        response.setDisplayOrder(faq.getDisplayOrder());
        response.setActive(faq.getActive());
        response.setCreatedAt(faq.getCreatedAt());
        response.setUpdatedAt(faq.getUpdatedAt());
        return response;
    }
}