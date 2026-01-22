package com.acp.simccs.modules.communication.repository;

import com.acp.simccs.modules.communication.model.ChatMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    // Find messages for a specific channel, ordered by time
    List<ChatMessage> findByChannelIdOrderBySentAtAsc(Long channelId);
}