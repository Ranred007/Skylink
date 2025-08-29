package com.skylink.dao;

import com.skylink.entity.FAQ;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FAQRepository extends JpaRepository<FAQ, Long> {
    
    List<FAQ> findByActiveTrue();
    
    List<FAQ> findByActiveTrueOrderByDisplayOrderAsc();
    
    List<FAQ> findByCategory(String category);
    
    List<FAQ> findByCategoryAndActiveTrue(String category);
    
    @Query("SELECT DISTINCT f.category FROM FAQ f WHERE f.active = true")
    List<String> findDistinctCategories();
    
    @Query("SELECT f FROM FAQ f WHERE f.active = true AND f.category = :category ORDER BY f.displayOrder ASC")
    List<FAQ> findActiveFAQsByCategoryOrderByDisplayOrder(@Param("category") String category);
    
    @Query("SELECT f FROM FAQ f WHERE f.active = true AND (f.question LIKE %:keyword% OR f.answer LIKE %:keyword%)")
    List<FAQ> searchByKeyword(@Param("keyword") String keyword);
}