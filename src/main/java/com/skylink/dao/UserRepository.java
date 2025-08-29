package com.skylink.dao;

import com.skylink.entity.Role;
import com.skylink.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByEmail(String email);
    
    Optional<User> findByMobileNumber(String mobileNumber);
    
    boolean existsByEmail(String email);
    
    boolean existsByMobileNumber(String mobileNumber);
    
    List<User> findByRole(Role role);
    
    List<User> findByActiveTrue();
    
    @Query("SELECT u FROM User u WHERE u.role = :role AND u.active = true")
    List<User> findActiveUsersByRole(@Param("role") Role role);
    
    @Query("SELECT COUNT(u) FROM User u WHERE u.role = :role")
    long countByRole(@Param("role") Role role);
    
    @Query("SELECT u FROM User u WHERE u.name LIKE %:name% OR u.email LIKE %:email%")
    List<User> searchByNameOrEmail(@Param("name") String name, @Param("email") String email);
}