//package com.skylink.dao;
//
//import com.skylink.entity.Complaint;
//import com.skylink.entity.ComplaintStatus;
//import com.skylink.entity.Priority;
//import com.skylink.entity.User;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.data.repository.query.Param;
//import org.springframework.stereotype.Repository;
//
//import java.time.LocalDateTime;
//import java.util.List;
//
//@Repository
//public interface ComplaintRepository extends JpaRepository<Complaint, Long> {
//    
//    List<Complaint> findByUser(User user);
//    
//    List<Complaint> findByUserOrderByCreatedAtDesc(User user);
//    
//    List<Complaint> findByStatus(ComplaintStatus status);
//    
//    List<Complaint> findByPriority(Priority priority);
//    
//    List<Complaint> findByStatusOrderByCreatedAtDesc(ComplaintStatus status);
//    
//    @Query("SELECT c FROM Complaint c WHERE c.status IN ('OPEN', 'IN_PROGRESS') ORDER BY c.priority DESC, c.createdAt ASC")
//    List<Complaint> findPendingComplaintsByPriority();
//    
//    @Query("SELECT c FROM Complaint c WHERE c.createdAt BETWEEN :startDate AND :endDate")
//    List<Complaint> findComplaintsBetweenDates(@Param("startDate") LocalDateTime startDate, 
//                                              @Param("endDate") LocalDateTime endDate);
//    
//    @Query("SELECT COUNT(c) FROM Complaint c WHERE c.status = :status")
//    long countByStatus(@Param("status") ComplaintStatus status);
//    
//    @Query("SELECT COUNT(c) FROM Complaint c WHERE c.user.id = :userId AND c.status IN ('OPEN', 'IN_PROGRESS')")
//    long countPendingComplaintsByUserId(@Param("userId") Long userId);
//    
//    @Query("SELECT c FROM Complaint c WHERE c.subject LIKE %:keyword% OR c.description LIKE %:keyword%")
//    List<Complaint> searchByKeyword(@Param("keyword") String keyword);
//}
package com.skylink.dao;

import com.skylink.entity.Complaint;
import com.skylink.entity.ComplaintStatus;
import com.skylink.entity.Priority;
import com.skylink.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ComplaintRepository extends JpaRepository<Complaint, Long> {
    
    List<Complaint> findByUser(User user);
    
    List<Complaint> findByUserOrderByCreatedAtDesc(User user);
    
    List<Complaint> findByStatus(ComplaintStatus status);
    
    List<Complaint> findByPriority(Priority priority);
    
    List<Complaint> findByStatusOrderByCreatedAtDesc(ComplaintStatus status);
    
    @Query("SELECT c FROM Complaint c WHERE c.status IN (com.skylink.entity.ComplaintStatus.OPEN, com.skylink.entity.ComplaintStatus.IN_PROGRESS) ORDER BY c.priority DESC, c.createdAt ASC")
    List<Complaint> findPendingComplaintsByPriority();
    
    @Query("SELECT c FROM Complaint c WHERE c.createdAt BETWEEN :startDate AND :endDate")
    List<Complaint> findComplaintsBetweenDates(@Param("startDate") LocalDateTime startDate, 
                                              @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT COUNT(c) FROM Complaint c WHERE c.status = :status")
    long countByStatus(@Param("status") ComplaintStatus status);
    
    @Query("SELECT COUNT(c) FROM Complaint c WHERE c.user.id = :userId AND c.status IN (com.skylink.entity.ComplaintStatus.OPEN, com.skylink.entity.ComplaintStatus.IN_PROGRESS)")
    long countPendingComplaintsByUserId(@Param("userId") Long userId);
    
    @Query("SELECT c FROM Complaint c WHERE c.subject LIKE %:keyword% OR c.description LIKE %:keyword%")
    List<Complaint> searchByKeyword(@Param("keyword") String keyword);
}