package com.skylink.controller;

import com.skylink.dto.LoginRequest;
import com.skylink.dto.LoginResponse;
import com.skylink.dto.SignupRequest;
import com.skylink.dto.UserResponse;
import com.skylink.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class AuthControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private AuthController authController;

    private SignupRequest signupRequest;
    private UserResponse userResponse;
    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        signupRequest = new SignupRequest();
        signupRequest.setEmail("test@example.com");
        signupRequest.setPassword("password");
        signupRequest.setName("Test User");

        userResponse = new UserResponse();
        userResponse.setId(1L);
        userResponse.setEmail("test@example.com");
        userResponse.setName("Test User");

        loginRequest = new LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("password");
    }

    @Test
    void testSignup() {
        when(userService.signup(any(SignupRequest.class))).thenReturn(userResponse);

        ResponseEntity<UserResponse> response = authController.signup(signupRequest);

        assertEquals(201, response.getStatusCodeValue());
        assertEquals("test@example.com", response.getBody().getEmail());
        assertEquals("Test User", response.getBody().getName());
    }

    @Test
    void testLogin() {
        when(userService.login(any(LoginRequest.class))).thenReturn("dummy-token");
        when(userService.getUserByEmail("test@example.com")).thenReturn(userResponse);

        ResponseEntity<LoginResponse> response = authController.login(loginRequest);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("dummy-token", response.getBody().getToken());
        assertEquals("test@example.com", response.getBody().getUser().getEmail());
    }

    @Test
    void testLogout() {
        ResponseEntity<String> response = authController.logout();

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Logged out successfully", response.getBody());
    }
}
