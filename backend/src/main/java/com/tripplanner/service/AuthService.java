package com.tripplanner.service;

import com.tripplanner.dto.request.LoginRequest;
import com.tripplanner.dto.request.RegisterRequest;
import com.tripplanner.dto.response.AuthResponse;
import com.tripplanner.dto.response.UserResponse;
import com.tripplanner.entity.User;
import com.tripplanner.exception.EmailAlreadyExistsException;
import com.tripplanner.mapper.UserMapper;
import com.tripplanner.repository.UserRepository;
import com.tripplanner.security.CustomUserDetails;
import com.tripplanner.security.JwtUtil;
import com.tripplanner.util.UserRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);
    private static final String TOKEN_TYPE = "Bearer";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserMapper userMapper;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JwtUtil jwtUtil,
            UserMapper userMapper
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userMapper = userMapper;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        String normalizedEmail = normalizeEmail(request.getEmail());

        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new EmailAlreadyExistsException(normalizedEmail);
        }

        User user = new User();
        user.setName(request.getName().trim());
        user.setEmail(normalizedEmail);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(UserRole.USER);

        User savedUser = userRepository.save(user);
        log.info("Registered new user with email: {}", normalizedEmail);

        CustomUserDetails userDetails = new CustomUserDetails(savedUser);
        String accessToken = jwtUtil.generateToken(userDetails);

        return buildAuthResponse(accessToken, userMapper.toResponse(savedUser));
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        String normalizedEmail = normalizeEmail(request.getEmail());

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(normalizedEmail, request.getPassword())
        );

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        String accessToken = jwtUtil.generateToken(userDetails);

        User user = userRepository.findByEmail(normalizedEmail)
                .orElseThrow(() -> new IllegalStateException("Authenticated user not found"));

        log.info("User logged in: {}", normalizedEmail);

        return buildAuthResponse(accessToken, userMapper.toResponse(user));
    }

    private AuthResponse buildAuthResponse(String accessToken, UserResponse userResponse) {
        return new AuthResponse(
                accessToken,
                TOKEN_TYPE,
                jwtUtil.getExpirationMs(),
                userResponse
        );
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase();
    }
}
