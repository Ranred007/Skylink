package com.skylink.dto;

import jakarta.validation.constraints.NotBlank;

public class FAQRequest {
    @NotBlank(message = "Question is required")
    private String question;

    @NotBlank(message = "Answer is required")
    private String answer;

    @NotBlank(message = "Category is required")
    private String category;

    private Integer displayOrder = 0;

    // Constructors
    public FAQRequest() {}

    public FAQRequest(String question, String answer, String category, Integer displayOrder) {
        this.question = question;
        this.answer = answer;
        this.category = category;
        this.displayOrder = displayOrder;
    }

    // Getters and Setters
    public String getQuestion() { return question; }
    public void setQuestion(String question) { this.question = question; }

    public String getAnswer() { return answer; }
    public void setAnswer(String answer) { this.answer = answer; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public Integer getDisplayOrder() { return displayOrder; }
    public void setDisplayOrder(Integer displayOrder) { this.displayOrder = displayOrder; }
}