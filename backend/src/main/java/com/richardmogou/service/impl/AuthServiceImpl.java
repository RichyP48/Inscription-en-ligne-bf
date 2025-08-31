package com.richardmogou.service.impl;

import com.richardmogou.model.dto.AuthResponse;
import com.richardmogou.model.dto.LoginRequest;
import com.richardmogou.model.dto.RegisterRequest;
import com.richardmogou.model.entity.AuthProvider;
import com.richardmogou.model.entity.Role;
import com.richardmogou.model.entity.User;
import com.richardmogou.repository.UserRepository;
import com.richardmogou.security.JwtTokenProvider;
import com.richardmogou.service.AuthService;
import com.richardmogou.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthServiceImpl.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager; // Inject Auth Manager
    private final JwtTokenProvider jwtTokenProvider; // Inject JWT Provider
    private final EmailService emailService; // Inject EmailService

    @Override
    @Transactional // Ensure the operation is atomic
    public AuthResponse registerUser(RegisterRequest registerRequest) { // Change return type to AuthResponse
        log.info("Attempting to register user with email: {}", registerRequest.getEmail());

        // 1. Check if email already exists
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            log.warn("Registration failed: Email already exists - {}", registerRequest.getEmail());
            // Consider using a custom exception for better error handling upstream
            throw new IllegalArgumentException("Error: Email address is already taken!");
        }

        // 2. Create new user entity
        User user = User.builder()
                .firstName(registerRequest.getFirstName())
                .lastName(registerRequest.getLastName())
                .email(registerRequest.getEmail())
                .password(passwordEncoder.encode(registerRequest.getPassword())) // Encode password
                .roles(Set.of(Role.ROLE_APPLICANT)) // Default role for new registrations
                .provider(AuthProvider.LOCAL) // Registered via local form
                .enabled(true) // User is enabled by default (consider email verification later)
                .locked(false)
                .build();

        // 3. Save user to database
        User savedUser = userRepository.save(user);
        log.info("User registered successfully with ID: {}", savedUser.getId());

        // 4. Send welcome email asynchronously
        try {
            String subject = "Welcome to the Registration Platform!";
            String text = String.format("Hello %s,\n\nWelcome! Your registration was successful.\n\nThank you,\nThe Platform Team",
                                        savedUser.getFirstName());
            emailService.sendSimpleMessage(savedUser.getEmail(), subject, text);
            log.info("Welcome email queued for user ID: {}", savedUser.getId());
        } catch (Exception e) {
            // Log error but don't fail the registration process if email fails
            log.error("Failed to send welcome email to user ID {}: {}", savedUser.getId(), e.getMessage());
        }

        // 5. Generate JWT token for the newly registered user
        String jwt = jwtTokenProvider.generateTokenFromEmail(savedUser.getEmail());
        log.debug("Generated JWT token for newly registered user: {}", savedUser.getEmail());

        // 6. Return the token and user details in an AuthResponse DTO
        return AuthResponse.builder()
                .accessToken(jwt)
                .userId(savedUser.getId())
                .email(savedUser.getEmail())
                .roles(savedUser.getRoles().stream()
                       .map(role -> role.name()) // Map Role enum to String name
                       .collect(Collectors.toSet()))
                .build();
    }

    @Override
    public AuthResponse authenticateUser(LoginRequest loginRequest) {
        log.info("Attempting to authenticate user with email: {}", loginRequest.getEmail());

        // 1. Perform authentication using Spring Security's AuthenticationManager
        // This will use our CustomUserDetailsService and PasswordEncoder implicitly
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );

        // 2. If authentication is successful, set the Authentication object in the SecurityContext
        SecurityContextHolder.getContext().setAuthentication(authentication);
        log.info("User authenticated successfully: {}", loginRequest.getEmail());

        // 3. Generate JWT token
        String jwt = jwtTokenProvider.generateToken(authentication);
        log.debug("Generated JWT token for user: {}", loginRequest.getEmail());

        // 4. Get the authenticated user principal
        User userDetails = (User) authentication.getPrincipal();

        // 5. Return the token and user details in an AuthResponse DTO
        return AuthResponse.builder()
                .accessToken(jwt)
                .userId(userDetails.getId())
                .email(userDetails.getEmail())
                .roles(userDetails.getRoles().stream()
                       .map(role -> role.name()) // Map Role enum to String name
                       .collect(Collectors.toSet()))
                .build();
        // AuthenticationException will be thrown by authenticationManager.authenticate()
        // if credentials are invalid, which can be handled by a global exception handler (@ControllerAdvice)
    }
}