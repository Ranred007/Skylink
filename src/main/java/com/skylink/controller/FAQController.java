package com.skylink.controller;

import com.skylink.dto.FAQRequest;
import com.skylink.dto.FAQResponse;
import com.skylink.service.FAQService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/faqs")
@CrossOrigin(origins = "*")
public class FAQController {

    @Autowired
    private FAQService faqService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FAQResponse> createFAQ(@Valid @RequestBody FAQRequest request) {
        FAQResponse faq = faqService.createFAQ(request);
        return new ResponseEntity<>(faq, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<FAQResponse>> getAllFAQs() {
        List<FAQResponse> faqs = faqService.getAllFAQs();
        return ResponseEntity.ok(faqs);
    }

    @GetMapping("/active")
    public ResponseEntity<List<FAQResponse>> getActiveFAQs() {
        List<FAQResponse> faqs = faqService.getActiveFAQs();
        return ResponseEntity.ok(faqs);
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<FAQResponse>> getFAQsByCategory(@PathVariable String category) {
        List<FAQResponse> faqs = faqService.getFAQsByCategory(category);
        return ResponseEntity.ok(faqs);
    }

    @GetMapping("/categories")
    public ResponseEntity<List<String>> getAllCategories() {
        List<String> categories = faqService.getAllCategories();
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FAQResponse> getFAQById(@PathVariable Long id) {
        FAQResponse faq = faqService.getFAQById(id);
        return ResponseEntity.ok(faq);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FAQResponse> updateFAQ(@PathVariable Long id, 
                                                @Valid @RequestBody FAQRequest request) {
        FAQResponse faq = faqService.updateFAQ(id, request);
        return ResponseEntity.ok(faq);
    }

    @PutMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deactivateFAQ(@PathVariable Long id) {
        faqService.deactivateFAQ(id);
        return ResponseEntity.ok("FAQ deactivated successfully");
    }

    @PutMapping("/{id}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> activateFAQ(@PathVariable Long id) {
        faqService.activateFAQ(id);
        return ResponseEntity.ok("FAQ activated successfully");
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteFAQ(@PathVariable Long id) {
        faqService.deleteFAQ(id);
        return ResponseEntity.ok("FAQ deleted successfully");
    }

    @GetMapping("/search")
    public ResponseEntity<List<FAQResponse>> searchFAQs(@RequestParam String keyword) {
        List<FAQResponse> faqs = faqService.searchFAQs(keyword);
        return ResponseEntity.ok(faqs);
    }
}