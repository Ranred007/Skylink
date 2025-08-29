package com.skylink.controller;

import com.skylink.dto.ComplaintRequest;
import com.skylink.dto.ComplaintResponse;
import com.skylink.entity.ComplaintStatus;
import com.skylink.entity.Priority;
import com.skylink.service.ComplaintService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/complaints")
@CrossOrigin(origins = "*")
public class ComplaintController {

    @Autowired
    private ComplaintService complaintService;

    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('ADMIN')")
    public ResponseEntity<ComplaintResponse> createComplaint(@Valid @RequestBody ComplaintRequest request) {
        ComplaintResponse complaint = complaintService.createComplaint(request);
        return new ResponseEntity<>(complaint, HttpStatus.CREATED);
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.id")
    public ResponseEntity<List<ComplaintResponse>> getUserComplaints(@PathVariable Long userId) {
        List<ComplaintResponse> complaints = complaintService.getUserComplaints(userId);
        return ResponseEntity.ok(complaints);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ComplaintResponse>> getAllComplaints() {
        List<ComplaintResponse> complaints = complaintService.getAllComplaints();
        return ResponseEntity.ok(complaints);
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ComplaintResponse>> getComplaintsByStatus(@PathVariable ComplaintStatus status) {
        List<ComplaintResponse> complaints = complaintService.getComplaintsByStatus(status);
        return ResponseEntity.ok(complaints);
    }

    @GetMapping("/pending")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ComplaintResponse>> getPendingComplaints() {
        List<ComplaintResponse> complaints = complaintService.getPendingComplaints();
        return ResponseEntity.ok(complaints);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @complaintService.getComplaintById(#id).userId == authentication.principal.id")
    public ResponseEntity<ComplaintResponse> getComplaintById(@PathVariable Long id) {
        ComplaintResponse complaint = complaintService.getComplaintById(id);
        return ResponseEntity.ok(complaint);
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ComplaintResponse> updateComplaintStatus(@PathVariable Long id, 
                                                                  @RequestParam ComplaintStatus status,
                                                                  @RequestParam(required = false) String adminResponse) {
        ComplaintResponse complaint = complaintService.updateComplaintStatus(id, status, adminResponse);
        return ResponseEntity.ok(complaint);
    }

    @PutMapping("/{id}/priority")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ComplaintResponse> updateComplaintPriority(@PathVariable Long id, 
                                                                    @RequestParam Priority priority) {
        ComplaintResponse complaint = complaintService.updateComplaintPriority(id, priority);
        return ResponseEntity.ok(complaint);
    }

    @GetMapping("/search")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ComplaintResponse>> searchComplaints(@RequestParam String keyword) {
        List<ComplaintResponse> complaints = complaintService.searchComplaints(keyword);
        return ResponseEntity.ok(complaints);
    }

    @GetMapping("/stats/total")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Long> getTotalComplaints() {
        long count = complaintService.getTotalComplaints();
        return ResponseEntity.ok(count);
    }

    @GetMapping("/stats/status/{status}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ComplaintResponse>> getComplaintsByStatus1(@PathVariable ComplaintStatus status) {
        List<ComplaintResponse> count = complaintService.getComplaintsByStatus(status);
        return ResponseEntity.ok(count);
    }
}