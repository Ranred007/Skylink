package com.skylink.controller;

import com.skylink.dto.LoginRequest;
import com.skylink.dto.LoginResponse;
import com.skylink.dto.SignupRequest;
import com.skylink.dto.UserResponse;
import com.skylink.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<UserResponse> signup(@Valid @RequestBody SignupRequest request) {
        UserResponse user = userService.signup(request);
        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        String token = userService.login(request);
        UserResponse user = userService.getUserByEmail(request.getEmail());
        
        LoginResponse response = new LoginResponse();
        response.setToken(token);
        response.setUser(user);
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout() {
        // In a stateless JWT implementation, logout is handled client-side
        // by removing the token from storage
        return ResponseEntity.ok("Logged out successfully");
    }
}