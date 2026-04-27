package com.example.aitrainer.repository;

import com.example.aitrainer.model.ChatMessage;
import com.example.aitrainer.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    // Full history for display (oldest first)
    List<ChatMessage> findByUserOrderByTimestampAsc(User user);

    // Last 20 messages (10 exchanges) for AI context window — newest first, then we reverse
    List<ChatMessage> findTop20ByUserOrderByTimestampDesc(User user);

    // Delete all messages for a user (clear chat)
    void deleteByUser(User user);
}
