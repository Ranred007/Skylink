package com.skylink.dao;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.skylink.entity.FAQ;

@DataJpaTest
class FAQRepositoryTest {

    @Autowired
    private FAQRepository faqRepository;

    private FAQ activeFaq1;
    private FAQ activeFaq2;
    private FAQ inactiveFaq;
    private FAQ differentCategoryFaq;

    @BeforeEach
    void setUp() {
        // Clear any existing data
        faqRepository.deleteAll();

        // Create test FAQs using the actual entity structure
        activeFaq1 = createFAQ("How to reset password?", "Go to settings and click reset password", "Account", 1, true);
        activeFaq2 = createFAQ("How to update profile?", "Navigate to profile section and edit details", "Account", 2, true);
        inactiveFaq = createFAQ("Old feature question", "This feature is deprecated", "Account", 3, false);
        differentCategoryFaq = createFAQ("Billing inquiry", "Contact support for billing issues", "Billing", 1, true);
    }

    private FAQ createFAQ(String question, String answer, String category, int displayOrder, boolean active) {
        FAQ faq = new FAQ();
        faq.setQuestion(question);
        faq.setAnswer(answer);
        faq.setCategory(category);
        faq.setDisplayOrder(displayOrder);
        faq.setActive(active);
        return faqRepository.save(faq);
    }

    @Test
    void testFindByActiveTrue() {
        List<FAQ> activeFaqs = faqRepository.findByActiveTrue();
        assertThat(activeFaqs).hasSize(3);
        assertThat(activeFaqs).extracting(FAQ::getActive).containsOnly(true);
        assertThat(activeFaqs).extracting(FAQ::getId)
            .contains(activeFaq1.getId(), activeFaq2.getId(), differentCategoryFaq.getId());
    }

    @Test
    void testFindByActiveTrueOrderByDisplayOrderAsc() {
        List<FAQ> activeFaqs = faqRepository.findByActiveTrueOrderByDisplayOrderAsc();
        assertThat(activeFaqs).hasSize(3);
        
        // Verify order by displayOrder ascending
        assertThat(activeFaqs.get(0).getDisplayOrder()).isLessThanOrEqualTo(activeFaqs.get(1).getDisplayOrder());
        assertThat(activeFaqs.get(1).getDisplayOrder()).isLessThanOrEqualTo(activeFaqs.get(2).getDisplayOrder());
        
        // Verify all are active
        assertThat(activeFaqs).extracting(FAQ::getActive).containsOnly(true);
    }

    @Test
    void testFindByCategory() {
        List<FAQ> accountFaqs = faqRepository.findByCategory("Account");
        assertThat(accountFaqs).hasSize(3); // Includes both active and inactive
        assertThat(accountFaqs).extracting(FAQ::getCategory).containsOnly("Account");
        
        List<FAQ> billingFaqs = faqRepository.findByCategory("Billing");
        assertThat(billingFaqs).hasSize(1);
        assertThat(billingFaqs.get(0).getCategory()).isEqualTo("Billing");
        
        List<FAQ> nonExistentCategory = faqRepository.findByCategory("NonExistent");
        assertThat(nonExistentCategory).isEmpty();
    }

    @Test
    void testFindByCategoryAndActiveTrue() {
        List<FAQ> activeAccountFaqs = faqRepository.findByCategoryAndActiveTrue("Account");
        assertThat(activeAccountFaqs).hasSize(2); // Only active ones
        assertThat(activeAccountFaqs).extracting(FAQ::getCategory).containsOnly("Account");
        assertThat(activeAccountFaqs).extracting(FAQ::getActive).containsOnly(true);
        
        List<FAQ> activeBillingFaqs = faqRepository.findByCategoryAndActiveTrue("Billing");
        assertThat(activeBillingFaqs).hasSize(1);
        assertThat(activeBillingFaqs.get(0).getCategory()).isEqualTo("Billing");
        
        List<FAQ> nonExistentCategory = faqRepository.findByCategoryAndActiveTrue("NonExistent");
        assertThat(nonExistentCategory).isEmpty();
    }

    @Test
    void testFindDistinctCategories() {
        List<String> categories = faqRepository.findDistinctCategories();
        assertThat(categories).hasSize(2);
        assertThat(categories).containsExactlyInAnyOrder("Account", "Billing");
    }

    @Test
    void testFindActiveFAQsByCategoryOrderByDisplayOrder() {
        List<FAQ> accountFaqs = faqRepository.findActiveFAQsByCategoryOrderByDisplayOrder("Account");
        assertThat(accountFaqs).hasSize(2);
        assertThat(accountFaqs).extracting(FAQ::getCategory).containsOnly("Account");
        assertThat(accountFaqs).extracting(FAQ::getActive).containsOnly(true);
        
        // Verify order by displayOrder ascending
        assertThat(accountFaqs.get(0).getDisplayOrder()).isLessThanOrEqualTo(accountFaqs.get(1).getDisplayOrder());
        
        List<FAQ> billingFaqs = faqRepository.findActiveFAQsByCategoryOrderByDisplayOrder("Billing");
        assertThat(billingFaqs).hasSize(1);
        assertThat(billingFaqs.get(0).getCategory()).isEqualTo("Billing");
        
        List<FAQ> nonExistentCategory = faqRepository.findActiveFAQsByCategoryOrderByDisplayOrder("NonExistent");
        assertThat(nonExistentCategory).isEmpty();
    }

    @Test
    void testSearchByKeyword() {
        // Search in question
        List<FAQ> passwordResults = faqRepository.searchByKeyword("password");
        assertThat(passwordResults).hasSize(1);
        assertThat(passwordResults.get(0).getId()).isEqualTo(activeFaq1.getId());
        
        // Search in answer
        List<FAQ> profileResults = faqRepository.searchByKeyword("profile");
        assertThat(profileResults).hasSize(1);
        assertThat(profileResults.get(0).getId()).isEqualTo(activeFaq2.getId());
        
        // Search in both question and answer
        List<FAQ> supportResults = faqRepository.searchByKeyword("support");
        assertThat(supportResults).hasSize(1);
        assertThat(supportResults.get(0).getId()).isEqualTo(differentCategoryFaq.getId());
        
        // Search with multiple matches
        List<FAQ> accountResults = faqRepository.searchByKeyword("account");
        assertThat(accountResults).hasSize(0); // Both active account FAQs
    }

    @Test
    void testSearchByKeyword_NoMatches() {
        List<FAQ> results = faqRepository.searchByKeyword("nonexistent");
        assertThat(results).isEmpty();
    }

    @Test
    void testSearchByKeyword_OnlyActive() {
        // Search for a term that exists in inactive FAQ
        List<FAQ> results = faqRepository.searchByKeyword("deprecated");
        assertThat(results).isEmpty(); // Should not find inactive FAQ
    }

    @Test
    void testSaveAndRetrieveFAQ() {
        FAQ newFaq = new FAQ();
        newFaq.setQuestion("New question");
        newFaq.setAnswer("New answer");
        newFaq.setCategory("Technical");
        newFaq.setDisplayOrder(1);
        newFaq.setActive(true);
        
        FAQ savedFaq = faqRepository.save(newFaq);
        
        // Retrieve using repository method
        FAQ retrievedFaq = faqRepository.findById(savedFaq.getId()).orElseThrow();
        
        assertThat(retrievedFaq.getQuestion()).isEqualTo("New question");
        assertThat(retrievedFaq.getAnswer()).isEqualTo("New answer");
        assertThat(retrievedFaq.getCategory()).isEqualTo("Technical");
        assertThat(retrievedFaq.getDisplayOrder()).isEqualTo(1);
        assertThat(retrievedFaq.getActive()).isTrue();
    }

    @Test
    void testUpdateFAQ() {
        // Get existing FAQ
        FAQ faq = faqRepository.findById(activeFaq1.getId()).orElseThrow();
        
        // Update fields
        faq.setQuestion("Updated question");
        faq.setAnswer("Updated answer");
        faq.setCategory("UpdatedCategory");
        faq.setDisplayOrder(99);
        faq.setActive(false);
        
        FAQ updatedFaq = faqRepository.save(faq);
        
        // Verify updates
        assertThat(updatedFaq.getQuestion()).isEqualTo("Updated question");
        assertThat(updatedFaq.getAnswer()).isEqualTo("Updated answer");
        assertThat(updatedFaq.getCategory()).isEqualTo("UpdatedCategory");
        assertThat(updatedFaq.getDisplayOrder()).isEqualTo(99);
        assertThat(updatedFaq.getActive()).isFalse();
        
        // Verify it's no longer in active results
        List<FAQ> activeFaqs = faqRepository.findByActiveTrue();
        assertThat(activeFaqs).extracting(FAQ::getId).doesNotContain(activeFaq1.getId());
    }

    @Test
    void testDeleteFAQ() {
        long initialCount = faqRepository.count();
        faqRepository.deleteById(activeFaq1.getId());
        
        assertThat(faqRepository.count()).isEqualTo(initialCount - 1);
        assertThat(faqRepository.findById(activeFaq1.getId())).isEmpty();
        
        // Verify it's removed from category searches too
        List<FAQ> accountFaqs = faqRepository.findByCategory("Account");
        assertThat(accountFaqs).extracting(FAQ::getId).doesNotContain(activeFaq1.getId());
    }

    @Test
    void testFindByCategory_EmptyCategory() {
        FAQ faqWithEmptyCategory = createFAQ("Test question", "Test answer", "", 1, true);
        
        List<FAQ> emptyCategoryFaqs = faqRepository.findByCategory("");
        assertThat(emptyCategoryFaqs).hasSize(1);
        assertThat(emptyCategoryFaqs.get(0).getId()).isEqualTo(faqWithEmptyCategory.getId());
    }

    @Test
    void testSearchByKeyword_SpecialCharacters() {
        FAQ specialFaq = createFAQ("How to use @ symbol?", "Use @ in emails", "Technical", 1, true);
        
        List<FAQ> results = faqRepository.searchByKeyword("@");
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getId()).isEqualTo(specialFaq.getId());
    }
}