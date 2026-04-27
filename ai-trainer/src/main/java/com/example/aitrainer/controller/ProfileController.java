package com.example.aitrainer.controller;

import com.example.aitrainer.dto.ProfileRequest;
import com.example.aitrainer.dto.ProfileResponse;
import com.example.aitrainer.service.ProfileService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    // Save or update profile — requires JWT in header
    @PostMapping
    public ResponseEntity<ProfileResponse> saveProfile(@Valid @RequestBody ProfileRequest request) {
        ProfileResponse response = profileService.saveProfile(request);
        return ResponseEntity.ok(response);
    }

    // Get my profile + stats — requires JWT in header
    @GetMapping("/me")
    public ResponseEntity<ProfileResponse> getMyProfile() {
        ProfileResponse response = profileService.getMyProfile();
        return ResponseEntity.ok(response);
    }
}
