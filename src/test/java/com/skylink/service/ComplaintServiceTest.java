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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ComplaintServiceTest {

    @Mock
    private ComplaintRepository complaintRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ComplaintService complaintService;

    private User testUser;
    private Complaint testComplaint;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        testUser = new User();
        testUser.setId(1L);
        testUser.setName("John Doe");
        testUser.setEmail("john@example.com");

        testComplaint = new Complaint();
        testComplaint.setId(1L);
        testComplaint.setUser(testUser);
        testComplaint.setSubject("Network Issue");
        testComplaint.setDescription("Internet not working");
        testComplaint.setPriority(Priority.HIGH);
        testComplaint.setStatus(ComplaintStatus.IN_PROGRESS);
        testComplaint.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void testCreateComplaint() {
        ComplaintRequest request = new ComplaintRequest();
        request.setUserId(1L);
        request.setSubject("Network Issue");
        request.setDescription("Internet not working");
        request.setPriority(Priority.HIGH);

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(complaintRepository.save(any(Complaint.class))).thenReturn(testComplaint);

        ComplaintResponse response = complaintService.createComplaint(request);

        assertNotNull(response);
        assertEquals("Network Issue", response.getSubject());
        assertEquals("John Doe", response.getUserName());
    }

    @Test
    void testCreateComplaint_UserNotFound() {
        ComplaintRequest request = new ComplaintRequest();
        request.setUserId(99L);

        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> complaintService.createComplaint(request));
    }

    @Test
    void testGetUserComplaints() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(complaintRepository.findByUserOrderByCreatedAtDesc(testUser))
                .thenReturn(Collections.singletonList(testComplaint));

        var complaints = complaintService.getUserComplaints(1L);

        assertEquals(1, complaints.size());
        assertEquals("Network Issue", complaints.get(0).getSubject());
    }

    @Test
    void testGetAllComplaints() {
        when(complaintRepository.findAll()).thenReturn(Collections.singletonList(testComplaint));

        var complaints = complaintService.getAllComplaints();

        assertEquals(1, complaints.size());
    }

    @Test
    void testGetComplaintById() {
        when(complaintRepository.findById(1L)).thenReturn(Optional.of(testComplaint));

        ComplaintResponse response = complaintService.getComplaintById(1L);

        assertEquals("Network Issue", response.getSubject());
    }

    @Test
    void testUpdateComplaintStatus() {
        when(complaintRepository.findById(1L)).thenReturn(Optional.of(testComplaint));
        when(complaintRepository.save(any(Complaint.class))).thenReturn(testComplaint);

        ComplaintResponse response = complaintService.updateComplaintStatus(1L, ComplaintStatus.RESOLVED, "Fixed");

        assertEquals(ComplaintStatus.RESOLVED, response.getStatus());
        assertEquals("Fixed", response.getAdminResponse());
    }

    @Test
    void testUpdateComplaintPriority() {
        when(complaintRepository.findById(1L)).thenReturn(Optional.of(testComplaint));
        when(complaintRepository.save(any(Complaint.class))).thenReturn(testComplaint);

        ComplaintResponse response = complaintService.updateComplaintPriority(1L, Priority.LOW);

        assertEquals(Priority.LOW, response.getPriority());
    }

    @Test
    void testSearchComplaints() {
        when(complaintRepository.searchByKeyword("Network"))
                .thenReturn(Arrays.asList(testComplaint));

        var complaints = complaintService.searchComplaints("Network");

        assertEquals(1, complaints.size());
        assertEquals("Network Issue", complaints.get(0).getSubject());
    }

    @Test
    void testGetTotalComplaints() {
        when(complaintRepository.count()).thenReturn(5L);

        assertEquals(5, complaintService.getTotalComplaints());
    }

    @Test
    void testGetComplaintsByStatusCount() {
        when(complaintRepository.countByStatus(ComplaintStatus.IN_PROGRESS)).thenReturn(3L);

        assertEquals(3, complaintService.getComplaintsByStatus1(ComplaintStatus.IN_PROGRESS));
    }
}
