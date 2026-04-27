package com.example.aitrainer.repository;

import com.example.aitrainer.model.Plan;
import com.example.aitrainer.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlanRepository extends JpaRepository<Plan, Long> {
    // Get all plans for a user (newest first)
    List<Plan> findByUserOrderByGeneratedAtDesc(User user);
    // Get only the most recent plan
    Optional<Plan> findFirstByUserOrderByGeneratedAtDesc(User user);
}
