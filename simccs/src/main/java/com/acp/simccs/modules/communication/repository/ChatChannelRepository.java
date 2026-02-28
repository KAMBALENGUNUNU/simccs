package com.acp.simccs.modules.communication.repository;

import com.acp.simccs.modules.communication.model.ChatChannel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ChatChannelRepository extends JpaRepository<ChatChannel, Long> {
    Optional<ChatChannel> findByName(String name);

    Optional<ChatChannel> findByReportId(Long reportId);
}