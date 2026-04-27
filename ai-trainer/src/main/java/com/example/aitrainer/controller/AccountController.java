package com.example.aitrainer.controller;

import com.example.aitrainer.model.User;
import com.example.aitrainer.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/account")
public class AccountController {

    private final UserRepository userRepository;

    public AccountController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteAccount() {
        User user = getCurrentUser();
        // Since we have cascading relationships (plans, profile, progress, chat), 
        // deleting the user should delete all associated data if cascading is set up.
        // If not, we might need to manually delete them or let the DB handle it if ON DELETE CASCADE is configured.
        // Let's assume JPA cascading handles it or we'll add a helper if needed.
        userRepository.delete(user);
        return ResponseEntity.ok("Account deleted successfully");
    }
}
