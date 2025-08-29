package com.skylink.controller;

import com.skylink.dto.SignupRequest;
import com.skylink.dto.UserResponse;
import com.skylink.entity.Role;
import com.skylink.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<UserResponse> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        UserResponse user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/role/{role}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponse>> getUsersByRole(@PathVariable Role role) {
        List<UserResponse> users = userService.getUsersByRole(role);
        return ResponseEntity.ok(users);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
    public ResponseEntity<UserResponse> updateUser(@PathVariable Long id, 
                                                  @Valid @RequestBody SignupRequest request) {
        UserResponse user = userService.updateUser(id, request);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deactivateUser(@PathVariable Long id) {
        userService.deactivateUser(id);
        return ResponseEntity.ok("User deactivated successfully");
    }

    @PutMapping("/{id}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> activateUser(@PathVariable Long id) {
        userService.activateUser(id);
        return ResponseEntity.ok("User activated successfully");
    }

    @GetMapping("/stats/customers")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Long> getTotalCustomers() {
        long count = userService.getTotalCustomers();
        return ResponseEntity.ok(count);
    }

    @GetMapping("/stats/admins")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Long> getTotalAdmins() {
        long count = userService.getTotalAdmins();
        return ResponseEntity.ok(count);
    }
}