package com.skylink.service;

import com.skylink.entity.User;

import io.jsonwebtoken.ExpiredJwtException;

import com.skylink.entity.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;
    private User testUser;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        // manually inject test secret & expiration since @Value won't work in unit test without Spring context
        jwtService.secret = "MySuperSecretKeyForJwtAuth1234567890123456"; // must be 32+ chars
        jwtService.expiration = 1000L * 60 * 60; // 1 hour

        testUser = new User();
        testUser.setId(1L);
        testUser.setName("Swetha");
        testUser.setEmail("swetha@example.com");
        testUser.setRole(Role.CUSTOMER);
    }

    @Test
    void testGenerateTokenAndExtractClaims() {
        String token = jwtService.generateToken(testUser);

        assertNotNull(token);

        String username = jwtService.extractUsername(token);
        assertEquals("swetha@example.com", username);

        Long userId = jwtService.extractUserId(token);
        assertEquals(1L, userId);

        String role = jwtService.extractRole(token);
        assertEquals("CUSTOMER", role);
    }

    @Test
    void testTokenValidation() {
        String token = jwtService.generateToken(testUser);

        UserDetails mockUserDetails = Mockito.mock(UserDetails.class);
        Mockito.when(mockUserDetails.getUsername()).thenReturn("swetha@example.com");

        boolean isValid = jwtService.validateToken(token, mockUserDetails);
        assertTrue(isValid);
    }

    @Test
    void testInvalidTokenValidation() {
        String token = jwtService.generateToken(testUser);

        UserDetails mockUserDetails = Mockito.mock(UserDetails.class);
        Mockito.when(mockUserDetails.getUsername()).thenReturn("wronguser@example.com");

        boolean isValid = jwtService.validateToken(token, mockUserDetails);
        assertFalse(isValid);
    }

//    @Test
//    void testExpiredToken() throws InterruptedException {
//        
//        jwtService.expiration = 5L;
//        String token = jwtService.generateToken(testUser);
//
//        Thread.sleep(5); // wait so it expires
//
//        UserDetails mockUserDetails = Mockito.mock(UserDetails.class);
//        Mockito.when(mockUserDetails.getUsername()).thenReturn("swetha@example.com");
//
//        boolean isValid = jwtService.validateToken(token, mockUserDetails);
//        assertFalse(isValid);
//    }
    @Test
    void expiredToken_throwsExpiredJwtException() {
        // set token expiry to a negative value (already expired)
        ReflectionTestUtils.setField(jwtService, "expiration", -1000L);

        String token = jwtService.generateToken(testUser);

        UserDetails userDetails = Mockito.mock(UserDetails.class);
        Mockito.when(userDetails.getUsername()).thenReturn("swetha@example.com");

        // The call should throw ExpiredJwtException
        assertThrows(ExpiredJwtException.class,
                () -> jwtService.validateToken(token, userDetails));
    }

}
