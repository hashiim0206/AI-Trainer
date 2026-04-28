package com.example.aitrainer.dto;

// Combined response: profile info + all calculated stats
public class ProfileResponse {

    // Who they are
    private String username;
    private String fullName;
    private java.time.LocalDate dateOfBirth;
    private int age;
    private String gender;
    private Double heightCm;
    private Double weightKg;
    private Double targetWeightKg;
    private String trainingLevel;
    private String dietPreference;
    private String sports;
    private String country;

    // Their health stats (calculated by us)
    private StatsResult stats;
    
    // Login Streak
    private Integer currentStreak;

    // Constructors
    public ProfileResponse() {}

    // Getters and Setters
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public java.time.LocalDate getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(java.time.LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public Double getHeightCm() { return heightCm; }
    public void setHeightCm(Double heightCm) { this.heightCm = heightCm; }

    public Double getWeightKg() { return weightKg; }
    public void setWeightKg(Double weightKg) { this.weightKg = weightKg; }

    public Double getTargetWeightKg() { return targetWeightKg; }
    public void setTargetWeightKg(Double targetWeightKg) { this.targetWeightKg = targetWeightKg; }

    public String getTrainingLevel() { return trainingLevel; }
    public void setTrainingLevel(String trainingLevel) { this.trainingLevel = trainingLevel; }

    public String getDietPreference() { return dietPreference; }
    public void setDietPreference(String dietPreference) { this.dietPreference = dietPreference; }

    public String getSports() { return sports; }
    public void setSports(String sports) { this.sports = sports; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public StatsResult getStats() { return stats; }
    public void setStats(StatsResult stats) { this.stats = stats; }

    public Integer getCurrentStreak() { return currentStreak; }
    public void setCurrentStreak(Integer currentStreak) { this.currentStreak = currentStreak; }
}
