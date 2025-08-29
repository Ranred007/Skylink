package com.skylink.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.skylink.dao.UserRepository;
import com.skylink.dto.LoginRequest;
import com.skylink.dto.SignupRequest;
import com.skylink.dto.UserResponse;
import com.skylink.entity.Role;
import com.skylink.entity.User;
import com.skylink.exception.ResourceNotFoundException;
import com.skylink.exception.UserAlreadyExistsException;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private SignupRequest signupRequest;
    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        testUser = new User("John Doe", "john@example.com", "1234567890", "password", Role.CUSTOMER);
        testUser.setId(1L);
        testUser.setActive(true);
        testUser.setCreatedAt(LocalDateTime.now());
        testUser.setUpdatedAt(LocalDateTime.now());

        signupRequest = new SignupRequest();
        signupRequest.setName("John Doe");
        signupRequest.setEmail("john@example.com");
        signupRequest.setMobileNumber("1234567890");
        signupRequest.setPassword("password");
        signupRequest.setRole(Role.CUSTOMER);

        loginRequest = new LoginRequest();
        loginRequest.setEmail("john@example.com");
        loginRequest.setPassword("password");
    }

    @Test
    void testLoadUserByUsername_Success() {
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(testUser));

        UserDetails userDetails = userService.loadUserByUsername("john@example.com");

        assertThat(userDetails).isNotNull();
        assertThat(userDetails.getUsername()).isEqualTo("john@example.com");
        verify(userRepository).findByEmail("john@example.com");
    }

    @Test
    void testLoadUserByUsername_UserNotFound() {
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> {
            userService.loadUserByUsername("nonexistent@example.com");
        });

        verify(userRepository).findByEmail("nonexistent@example.com");
    }

    @Test
    void testSignup_Success() {
        when(userRepository.existsByEmail("john@example.com")).thenReturn(false);
        when(userRepository.existsByMobileNumber("1234567890")).thenReturn(false);
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        UserResponse response = userService.signup(signupRequest);

        assertThat(response).isNotNull();
        assertThat(response.getEmail()).isEqualTo("john@example.com");
        assertThat(response.getName()).isEqualTo("John Doe");
        assertThat(response.getRole()).isEqualTo(Role.CUSTOMER);

        verify(userRepository).existsByEmail("john@example.com");
        verify(userRepository).existsByMobileNumber("1234567890");
        verify(passwordEncoder).encode("password");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void testSignup_EmailAlreadyExists() {
        when(userRepository.existsByEmail("john@example.com")).thenReturn(true);

        assertThrows(UserAlreadyExistsException.class, () -> {
            userService.signup(signupRequest);
        });

        verify(userRepository).existsByEmail("john@example.com");
        verify(userRepository, never()).existsByMobileNumber(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testSignup_MobileNumberAlreadyExists() {
        when(userRepository.existsByEmail("john@example.com")).thenReturn(false);
        when(userRepository.existsByMobileNumber("1234567890")).thenReturn(true);

        assertThrows(UserAlreadyExistsException.class, () -> {
            userService.signup(signupRequest);
        });

        verify(userRepository).existsByEmail("john@example.com");
        verify(userRepository).existsByMobileNumber("1234567890");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testSignup_DefaultRole() {
        signupRequest.setRole(null); // Test default role assignment

        when(userRepository.existsByEmail("john@example.com")).thenReturn(false);
        when(userRepository.existsByMobileNumber("1234567890")).thenReturn(false);
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        UserResponse response = userService.signup(signupRequest);

        assertThat(response.getRole()).isEqualTo(Role.CUSTOMER);
    }

    @Test
    void testLogin_Success() {
        Authentication authentication = new UsernamePasswordAuthenticationToken(testUser, null);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .thenReturn(authentication);
        when(jwtService.generateToken(testUser)).thenReturn("jwtToken");

        String token = userService.login(loginRequest);

        assertThat(token).isEqualTo("jwtToken");
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtService).generateToken(testUser);
    }

    @Test
    void testGetUserById_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        UserResponse response = userService.getUserById(1L);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getEmail()).isEqualTo("john@example.com");
        verify(userRepository).findById(1L);
    }

    @Test
    void testGetUserById_NotFound() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            userService.getUserById(999L);
        });

        verify(userRepository).findById(999L);
    }

    @Test
    void testGetUserByEmail_Success() {
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(testUser));

        UserResponse response = userService.getUserByEmail("john@example.com");

        assertThat(response).isNotNull();
        assertThat(response.getEmail()).isEqualTo("john@example.com");
        verify(userRepository).findByEmail("john@example.com");
    }

    @Test
    void testGetUserByEmail_NotFound() {
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            userService.getUserByEmail("nonexistent@example.com");
        });

        verify(userRepository).findByEmail("nonexistent@example.com");
    }

    @Test
    void testGetAllUsers() {
        User adminUser = new User("Admin", "admin@example.com", "0987654321", "adminpass", Role.ADMIN);
        adminUser.setId(2L);

        when(userRepository.findAll()).thenReturn(List.of(testUser, adminUser));

        List<UserResponse> responses = userService.getAllUsers();

        assertThat(responses).hasSize(2);
        assertThat(responses).extracting(UserResponse::getEmail)
            .contains("john@example.com", "admin@example.com");
        verify(userRepository).findAll();
    }

    @Test
    void testGetUsersByRole() {
        User adminUser = new User("Admin", "admin@example.com", "0987654321", "adminpass", Role.ADMIN);
        adminUser.setId(2L);

        when(userRepository.findByRole(Role.ADMIN)).thenReturn(List.of(adminUser));

        List<UserResponse> responses = userService.getUsersByRole(Role.ADMIN);

        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getRole()).isEqualTo(Role.ADMIN);
        verify(userRepository).findByRole(Role.ADMIN);
    }

    @Test
    void testUpdateUser_Success() {
        SignupRequest updateRequest = new SignupRequest();
        updateRequest.setName("John Updated");
        updateRequest.setEmail("john.updated@example.com");
        updateRequest.setMobileNumber("1111111111");
        updateRequest.setPassword("newpassword");

        User updatedUser = new User("John Updated", "john.updated@example.com", "1111111111", "newpassword", Role.CUSTOMER);
        updatedUser.setId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.existsByEmail("john.updated@example.com")).thenReturn(false);
        when(userRepository.existsByMobileNumber("1111111111")).thenReturn(false);
        when(passwordEncoder.encode("newpassword")).thenReturn("encodedNewPassword");
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);

        UserResponse response = userService.updateUser(1L, updateRequest);

        assertThat(response.getName()).isEqualTo("John Updated");
        assertThat(response.getEmail()).isEqualTo("john.updated@example.com");
        assertThat(response.getMobileNumber()).isEqualTo("1111111111");

        verify(userRepository).findById(1L);
        verify(userRepository).existsByEmail("john.updated@example.com");
        verify(userRepository).existsByMobileNumber("1111111111");
        verify(passwordEncoder).encode("newpassword");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void testUpdateUser_WithoutPasswordChange() {
        SignupRequest updateRequest = new SignupRequest();
        updateRequest.setName("John Updated");
        updateRequest.setEmail("john@example.com"); // Same email
        updateRequest.setMobileNumber("1234567890"); // Same mobile
        updateRequest.setPassword(null); // No password change

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        UserResponse response = userService.updateUser(1L, updateRequest);

        assertThat(response.getName()).isEqualTo("John Updated");
        // Email and mobile should remain the same
        assertThat(response.getEmail()).isEqualTo("john@example.com");
        assertThat(response.getMobileNumber()).isEqualTo("1234567890");

        verify(userRepository, never()).existsByEmail(anyString()); // Should not check for same email
        verify(userRepository, never()).existsByMobileNumber(anyString()); // Should not check for same mobile
        verify(passwordEncoder, never()).encode(anyString()); // Should not encode password
    }

    @Test
    void testUpdateUser_EmailAlreadyExists() {
        SignupRequest updateRequest = new SignupRequest();
        updateRequest.setName("John Updated");
        updateRequest.setEmail("existing@example.com"); // Different email that exists
        updateRequest.setMobileNumber("1234567890");

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.existsByEmail("existing@example.com")).thenReturn(true);

        assertThrows(UserAlreadyExistsException.class, () -> {
            userService.updateUser(1L, updateRequest);
        });

        verify(userRepository).existsByEmail("existing@example.com");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testDeactivateUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        userService.deactivateUser(1L);

        verify(userRepository).findById(1L);
        verify(userRepository).save(any(User.class));
        // Verify user is set to inactive
        assertThat(testUser.getActive()).isFalse();
    }

    @Test
    void testActivateUser() {
        testUser.setActive(false); // Start with inactive user
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        userService.activateUser(1L);

        verify(userRepository).findById(1L);
        verify(userRepository).save(any(User.class));
        // Verify user is set to active
        assertThat(testUser.getActive()).isTrue();
    }

    @Test
    void testGetTotalCustomers() {
        when(userRepository.countByRole(Role.CUSTOMER)).thenReturn(5L);

        long count = userService.getTotalCustomers();

        assertThat(count).isEqualTo(5L);
        verify(userRepository).countByRole(Role.CUSTOMER);
    }

    @Test
    void testGetTotalAdmins() {
        when(userRepository.countByRole(Role.ADMIN)).thenReturn(2L);

        long count = userService.getTotalAdmins();

        assertThat(count).isEqualTo(2L);
        verify(userRepository).countByRole(Role.ADMIN);
    }

    @Test
    void testConvertToUserResponse() {
        UserResponse response = userService.convertToUserResponse(testUser);

        assertThat(response.getId()).isEqualTo(testUser.getId());
        assertThat(response.getName()).isEqualTo(testUser.getName());
        assertThat(response.getEmail()).isEqualTo(testUser.getEmail());
        assertThat(response.getMobileNumber()).isEqualTo(testUser.getMobileNumber());
        assertThat(response.getRole()).isEqualTo(testUser.getRole());
        assertThat(response.getActive()).isEqualTo(testUser.getActive());
        assertThat(response.getCreatedAt()).isEqualTo(testUser.getCreatedAt());
        assertThat(response.getUpdatedAt()).isEqualTo(testUser.getUpdatedAt());
    }
}