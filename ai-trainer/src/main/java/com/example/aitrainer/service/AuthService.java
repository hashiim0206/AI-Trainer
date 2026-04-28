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

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtils jwtUtils) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
    }

    public String register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Error: Username is already taken!");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Error: Email is already registered!");
        }

        User user = new User(
                request.getUsername(),
                request.getEmail(),
                passwordEncoder.encode(request.getPassword()),
                "ROLE_USER"
        );

        userRepository.save(user);
        return "User registered successfully!";
    }

    // Login accepts username OR email
    public String login(String identifier, String password) {
        User user = userRepository.findByUsernameOrEmail(identifier)
                .orElseThrow(() -> new RuntimeException("Error: Invalid username/email or password!"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Error: Invalid username/email or password!");
        }

        // Streak Tracking Logic
        java.time.LocalDate today = java.time.LocalDate.now();
        if (user.getLastLoginDate() == null || user.getLastLoginDate().isBefore(today.minusDays(1))) {
            user.setCurrentStreak(1);
        } else if (user.getLastLoginDate().equals(today.minusDays(1))) {
            user.setCurrentStreak((user.getCurrentStreak() == null ? 0 : user.getCurrentStreak()) + 1);
        }
        // If they already logged in today, streak remains the same
        
        user.setLastLoginDate(today);
        userRepository.save(user);

        return jwtUtils.generateToken(user.getUsername());
    }
}
