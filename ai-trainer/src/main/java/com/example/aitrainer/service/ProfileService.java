package com.example.aitrainer.service;

import com.example.aitrainer.dto.ProfileRequest;
import com.example.aitrainer.dto.ProfileResponse;
import com.example.aitrainer.dto.StatsResult;
import com.example.aitrainer.model.Profile;
import com.example.aitrainer.model.User;
import com.example.aitrainer.repository.ProfileRepository;
import com.example.aitrainer.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Period;
import java.time.LocalDate;

@Service
public class ProfileService {

    private final ProfileRepository profileRepository;
    private final UserRepository userRepository;
    private final StatsCalculatorService statsCalculator;

    public ProfileService(ProfileRepository profileRepository,
                          UserRepository userRepository,
                          StatsCalculatorService statsCalculator) {
        this.profileRepository = profileRepository;
        this.userRepository = userRepository;
        this.statsCalculator = statsCalculator;
    }

    // Get the currently logged-in user from their JWT token
    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // Create or update profile
    public ProfileResponse saveProfile(ProfileRequest request) {
        User user = getCurrentUser();

        // If profile exists, update it. If not, create it.
        Profile profile = profileRepository.findByUser(user)
                .orElse(new Profile());

        profile.setUser(user);
        profile.setFullName(request.getFullName());
        profile.setDateOfBirth(request.getDateOfBirth());
        profile.setGender(request.getGender().toUpperCase());
        profile.setHeightCm(request.getHeightCm());
        profile.setWeightKg(request.getWeightKg());
        profile.setTrainingLevel(request.getTrainingLevel().toUpperCase());
        profile.setDietPreference(request.getDietPreference().toUpperCase());
        profile.setSports(request.getSports());
        profile.setCountry(request.getCountry());

        profileRepository.save(profile);

        return buildResponse(user, profile);
    }

    // Get current user's profile + stats
    public ProfileResponse getMyProfile() {
        User user = getCurrentUser();
        Profile profile = profileRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Profile not found. Please complete your profile first."));
        return buildResponse(user, profile);
    }

    // Build the response object: combine profile data + calculated stats
    private ProfileResponse buildResponse(User user, Profile profile) {
        StatsResult stats = statsCalculator.calculate(profile);

        ProfileResponse response = new ProfileResponse();
        response.setUsername(user.getUsername());
        response.setFullName(profile.getFullName());
        response.setDateOfBirth(profile.getDateOfBirth());
        response.setAge(stats.getAge());
        response.setGender(profile.getGender());
        response.setHeightCm(profile.getHeightCm());
        response.setWeightKg(profile.getWeightKg());
        response.setTrainingLevel(profile.getTrainingLevel());
        response.setDietPreference(profile.getDietPreference());
        response.setSports(profile.getSports());
        response.setCountry(profile.getCountry());
        response.setStats(stats);
        response.setCurrentStreak(user.getCurrentStreak() == null ? 0 : user.getCurrentStreak());

        return response;
    }
}
