package com.example.aitrainer.repository;

import com.example.aitrainer.model.ChatSession;
import com.example.aitrainer.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatSessionRepository extends JpaRepository<ChatSession, Long> {
    List<ChatSession> findByUserOrderByUpdatedAtDesc(User user);
    void deleteByUser(User user);
}
