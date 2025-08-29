package com.skylink.service;

import com.skylink.dao.ComplaintRepository;
import com.skylink.dao.UserRepository;
import com.skylink.dto.ComplaintRequest;
import com.skylink.dto.ComplaintResponse;
import com.skylink.entity.Complaint;
import com.skylink.entity.ComplaintStatus;
import com.skylink.entity.Priority;
import com.skylink.entity.User;
import com.skylink.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ComplaintService {

    @Autowired
    private ComplaintRepository complaintRepository;

    @Autowired
    private UserRepository userRepository;

    public ComplaintResponse createComplaint(ComplaintRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + request.getUserId()));

        Complaint complaint = new Complaint();
        complaint.setUser(user);
        complaint.setSubject(request.getSubject());
        complaint.setDescription(request.getDescription());
        complaint.setPriority(request.getPriority() != null ? request.getPriority() : Priority.MEDIUM);

        Complaint savedComplaint = complaintRepository.save(complaint);
        return convertToComplaintResponse(savedComplaint);
    }

    public List<ComplaintResponse> getUserComplaints(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        return complaintRepository.findByUserOrderByCreatedAtDesc(user).stream()
                .map(this::convertToComplaintResponse)
                .collect(Collectors.toList());
    }

    public List<ComplaintResponse> getAllComplaints() {
        return complaintRepository.findAll().stream()
                .map(this::convertToComplaintResponse)
                .collect(Collectors.toList());
    }

    public List<ComplaintResponse> getComplaintsByStatus(ComplaintStatus status) {
        return complaintRepository.findByStatusOrderByCreatedAtDesc(status).stream()
                .map(this::convertToComplaintResponse)
                .collect(Collectors.toList());
    }

    public List<ComplaintResponse> getPendingComplaints() {
        return complaintRepository.findPendingComplaintsByPriority().stream()
                .map(this::convertToComplaintResponse)
                .collect(Collectors.toList());
    }

    public ComplaintResponse getComplaintById(Long id) {
        Complaint complaint = complaintRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Complaint not found with id: " + id));
        return convertToComplaintResponse(complaint);
    }

    public ComplaintResponse updateComplaintStatus(Long id, ComplaintStatus status, String adminResponse) {
        Complaint complaint = complaintRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Complaint not found with id: " + id));

        complaint.setStatus(status);
        if (adminResponse != null && !adminResponse.trim().isEmpty()) {
            complaint.setAdminResponse(adminResponse);
        }

        Complaint updatedComplaint = complaintRepository.save(complaint);
        return convertToComplaintResponse(updatedComplaint);
    }

    public ComplaintResponse updateComplaintPriority(Long id, Priority priority) {
        Complaint complaint = complaintRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Complaint not found with id: " + id));

        complaint.setPriority(priority);
        Complaint updatedComplaint = complaintRepository.save(complaint);
        return convertToComplaintResponse(updatedComplaint);
    }

    public List<ComplaintResponse> searchComplaints(String keyword) {
        return complaintRepository.searchByKeyword(keyword).stream()
                .map(this::convertToComplaintResponse)
                .collect(Collectors.toList());
    }

    public long getTotalComplaints() {
        return complaintRepository.count();
    }

    public long getComplaintsByStatus1(ComplaintStatus status) {
        return complaintRepository.countByStatus(status);
    }

    private ComplaintResponse convertToComplaintResponse(Complaint complaint) {
        ComplaintResponse response = new ComplaintResponse();
        response.setId(complaint.getId());
        response.setUserId(complaint.getUser().getId());
        response.setUserName(complaint.getUser().getName());
        response.setUserEmail(complaint.getUser().getEmail());
        response.setSubject(complaint.getSubject());
        response.setDescription(complaint.getDescription());
        response.setStatus(complaint.getStatus());
        response.setPriority(complaint.getPriority());
        response.setAdminResponse(complaint.getAdminResponse());
        response.setCreatedAt(complaint.getCreatedAt());
        response.setUpdatedAt(complaint.getUpdatedAt());
        response.setResolvedAt(complaint.getResolvedAt());
        return response;
    }
}