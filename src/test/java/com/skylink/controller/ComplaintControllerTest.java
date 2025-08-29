//package com.skylink.controller;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.when;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MockMvc;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.skylink.dto.ComplaintRequest;
//import com.skylink.dto.ComplaintResponse;
//import com.skylink.entity.ComplaintStatus;
//import com.skylink.entity.Priority;
//import com.skylink.service.ComplaintService;
//
//@WebMvcTest(ComplaintController.class)
//class ComplaintControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    @MockBean
//    private ComplaintService complaintService;
//
//    @Test
//    void testCreateComplaint() throws Exception {
//        ComplaintRequest request = new ComplaintRequest();
//        request.setSubject("Internet issue");
//        request.setDescription("Very slow speed");
//        request.setPriority(Priority.MEDIUM);
//
//        ComplaintResponse response = new ComplaintResponse();
//        response.setId(1L);
//        response.setSubject("Internet issue");
//        response.setDescription("Very slow speed");
//        response.setStatus(ComplaintStatus.OPEN);
//        response.setPriority(Priority.MEDIUM);
//
//        when(complaintService.createComplaint(any(ComplaintRequest.class)))
//                .thenReturn(response);
//
//        mockMvc.perform(post("/api/complaints")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(request)))
//                .andExpect(status().isCreated())
//                .andExpect(jsonPath("$.id").value(1L))
//                .andExpect(jsonPath("$.subject").value("Internet issue"))
//                .andExpect(jsonPath("$.description").value("Very slow speed"))
//                .andExpect(jsonPath("$.status").value("OPEN"))
//                .andExpect(jsonPath("$.priority").value("MEDIUM"));
//    }
//}
package com.skylink.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skylink.dto.ComplaintRequest;
import com.skylink.dto.ComplaintResponse;
import com.skylink.entity.ComplaintStatus;
import com.skylink.entity.Priority;
import com.skylink.service.ComplaintService;

@WebMvcTest(ComplaintController.class)
public class ComplaintControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ComplaintService complaintService;

    @Test
    @WithMockUser  // Add mock user to bypass security
    void testCreateComplaint() throws Exception {
        // Create request
        ComplaintRequest request = new ComplaintRequest();
        request.setSubject("Internet issue");
        request.setDescription("Very slow speed");
        request.setPriority(Priority.MEDIUM);

        // Create expected response
        ComplaintResponse response = new ComplaintResponse();
        response.setId(1L);
        response.setSubject("Internet issue");
        response.setDescription("Very slow speed");
        response.setStatus(ComplaintStatus.OPEN);
        response.setPriority(Priority.MEDIUM);

        // Mock the service call
        when(complaintService.createComplaint(any(ComplaintRequest.class)))
                .thenReturn(response);

        // Perform the test
        mockMvc.perform(post("/api/complaints")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.subject").value("Internet issue"))
                .andExpect(jsonPath("$.description").value("Very slow speed"))
                .andExpect(jsonPath("$.status").value("OPEN"))
                .andExpect(jsonPath("$.priority").value("MEDIUM"));
    }
}