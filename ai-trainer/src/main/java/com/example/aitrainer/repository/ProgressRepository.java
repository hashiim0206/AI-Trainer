package com.example.aitrainer.repository;

import com.example.aitrainer.model.ProgressEntry;
import com.example.aitrainer.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProgressRepository extends JpaRepository<ProgressEntry, Long> {

    // All entries for a user — oldest first (for charts/graphs)
    List<ProgressEntry> findByUserOrderByCheckinDateAsc(User user);

    // Most recent entry — for "last week's weight" comparison
    Optional<ProgressEntry> findFirstByUserOrderByCheckinDateDesc(User user);

    // Count how many check-ins a user has done
    long countByUser(User user);
}
