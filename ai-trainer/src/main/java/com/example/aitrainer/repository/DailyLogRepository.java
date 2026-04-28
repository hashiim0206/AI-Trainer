package com.example.aitrainer.repository;

import com.example.aitrainer.model.DailyLog;
import com.example.aitrainer.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface DailyLogRepository extends JpaRepository<DailyLog, Long> {
    List<DailyLog> findByUserAndDateOrderByLoggedAtAsc(User user, LocalDate date);
    void deleteByUser(User user);
}
