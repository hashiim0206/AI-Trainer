package com.example.aitrainer.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "profiles")
public class Profile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // One-to-One: each user has exactly one profile
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false)
    private LocalDate dateOfBirth; // We calculate age from this

    @Column(nullable = false)
    private String gender; // MALE, FEMALE, OTHER

    @Column(nullable = false)
    private Double heightCm;

    @Column(nullable = false)
    private Double weightKg;

    @Column(nullable = true)
    private Double targetWeightKg;

    // BEGINNER, AMATEUR, INTERMEDIATE, PRO
    @Column(nullable = false)
    private String trainingLevel;

    // VEGETARIAN, EGGITARIAN, NON_VEGETARIAN, VEGAN
    @Column(nullable = false)
    private String dietPreference;

    private String sports; // e.g. "Football, Swimming"

    @Column(nullable = false)
    private String country;

    // Constructors
    public Profile() {}

    public Profile(User user, String fullName, LocalDate dateOfBirth, String gender,
                   Double heightCm, Double weightKg, Double targetWeightKg, String trainingLevel,
                   String dietPreference, String sports, String country) {
        this.user = user;
        this.fullName = fullName;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.heightCm = heightCm;
        this.weightKg = weightKg;
        this.targetWeightKg = targetWeightKg;
        this.trainingLevel = trainingLevel;
        this.dietPreference = dietPreference;
        this.sports = sports;
        this.country = country;
    }

    // Getters and Setters
    public Long getId() { return id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }

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
}
