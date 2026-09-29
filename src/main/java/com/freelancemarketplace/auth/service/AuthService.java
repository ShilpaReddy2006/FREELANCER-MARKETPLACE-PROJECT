package com.freelancemarketplace.auth.service;

import com.freelancemarketplace.auth.dto.LoginRequest;
import com.freelancemarketplace.auth.dto.LoginResponse;
import com.freelancemarketplace.auth.dto.RegisterRequest;
import com.freelancemarketplace.auth.dto.RegisterResponse;
import com.freelancemarketplace.security.JwtService;
import com.freelancemarketplace.user.entity.User;
import com.freelancemarketplace.user.entity.UserStatus;
import com.freelancemarketplace.user.repository.UserRepository;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    // -------------------------
    // REGISTER
    // -------------------------

    public RegisterResponse register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered");
        }

        User user = new User();

        user.setName(request.getName());
        user.setEmail(request.getEmail());

        String encodedPassword =
                passwordEncoder.encode(request.getPassword());

        user.setPassword(encodedPassword);

        user.setRole(request.getRole());
        user.setStatus(UserStatus.ACTIVE);

        User savedUser = userRepository.save(user);

        return new RegisterResponse(
                savedUser.getId(),
                savedUser.getName(),
                savedUser.getEmail(),
                savedUser.getRole()
        );
    }

    // -------------------------
    // LOGIN
    // -------------------------

    public LoginResponse login(LoginRequest request) {

        User user = userRepository
                .findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new RuntimeException("Invalid email or password"));

        boolean passwordMatches =
                passwordEncoder.matches(
                        request.getPassword(),
                        user.getPassword()
                );

        if (!passwordMatches) {
            throw new RuntimeException("Invalid email or password");
        }

        String token =
                jwtService.generateToken(
                        user.getId(),
                        user.getRole().name()
                );

        return new LoginResponse(
                token,
                "Bearer"
        );
    }
}