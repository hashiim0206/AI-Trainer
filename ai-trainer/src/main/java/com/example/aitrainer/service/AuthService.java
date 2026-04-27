package com.example.aitrainer.service;

import com.example.aitrainer.dto.RegisterRequest;
import com.example.aitrainer.model.User;
import com.example.aitrainer.repository.UserRepository;
import com.example.aitrainer.security.JwtUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    // Manual Constructor for Dependency Injection
    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtils jwtUtils) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
    }

    public String register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Error: Username is already taken!");
        }

        // Using normal constructor instead of Builder
        User user = new User(
                request.getUsername(),
                passwordEncoder.encode(request.getPassword()),
                "ROLE_USER"
        );

        userRepository.save(user);

        return "User registered successfully!";
    }

    public String login(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Error: Invalid username or password!"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Error: Invalid username or password!");
        }

        return jwtUtils.generateToken(username);
    }
}
