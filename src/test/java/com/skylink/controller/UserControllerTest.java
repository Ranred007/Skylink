package com.skylink.controller;

import com.skylink.dto.SignupRequest;
import com.skylink.dto.UserResponse;
import com.skylink.entity.Role;
import com.skylink.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserControllerTest {

    @InjectMocks
    private UserController userController;

    @Mock
    private UserService userService;

    private UserResponse userResponse;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        userResponse = new UserResponse();
        userResponse.setId(1L);
        userResponse.setName("Swetha");
        userResponse.setEmail("swetha@example.com");
        userResponse.setMobileNumber("9876543210");
        userResponse.setRole(Role.CUSTOMER);
        userResponse.setActive(true);
    }

    @Test
    void testGetUserById() {
        when(userService.getUserById(1L)).thenReturn(userResponse);

        ResponseEntity<UserResponse> response = userController.getUserById(1L);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Swetha", response.getBody().getName());
        assertEquals("swetha@example.com", response.getBody().getEmail());
        verify(userService, times(1)).getUserById(1L);
    }

    @Test
    void testGetUsersByRole() {
        when(userService.getUsersByRole(Role.CUSTOMER)).thenReturn(Arrays.asList(userResponse));

        ResponseEntity<List<UserResponse>> response = userController.getUsersByRole(Role.CUSTOMER);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().size());
        assertEquals("Swetha", response.getBody().get(0).getName());
        verify(userService, times(1)).getUsersByRole(Role.CUSTOMER);
    }
}
