package com.acp.simccs.modules.communication.service;

import com.acp.simccs.modules.communication.dto.MessageDTO;
import com.acp.simccs.modules.communication.model.ChatChannel;
import com.acp.simccs.modules.communication.model.ChatMessage;
import com.acp.simccs.modules.communication.repository.ChatChannelRepository;
import com.acp.simccs.modules.communication.repository.ChatMessageRepository;
import com.acp.simccs.modules.identity.model.User;
import com.acp.simccs.modules.identity.repository.UserRepository;
import com.acp.simccs.modules.identity.service.SecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChatService {

    @Autowired
    private ChatMessageRepository messageRepository;
    
    @Autowired
    private ChatChannelRepository channelRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SecurityService securityService;

    @Transactional
    public MessageDTO saveMessage(MessageDTO messageDto, String senderEmail) {
        User sender = userRepository.findByEmail(senderEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        ChatChannel channel = channelRepository.findById(messageDto.getChannelId())
                .orElseThrow(() -> new RuntimeException("Channel not found"));

        ChatMessage message = new ChatMessage();
        message.setSender(sender);
        message.setChannel(channel);
        
        // Encrypt content before saving
        message.setContentEncrypted(securityService.encrypt(messageDto.getContent()));
        
        ChatMessage savedMsg = messageRepository.save(message);

        // Return DTO with plain text for immediate broadcast
        return new MessageDTO(
                sender.getFullName(),
                messageDto.getContent(),
                channel.getId(),
                savedMsg.getSentAt().toString()
        );
    }
    
    public List<MessageDTO> getHistory(Long channelId) {
        return messageRepository.findByChannelIdOrderBySentAtAsc(channelId).stream()
                .map(msg -> new MessageDTO(
                        msg.getSender().getFullName(),
                        securityService.decrypt(msg.getContentEncrypted()), // Decrypt on read
                        msg.getChannel().getId(),
                        msg.getSentAt().toString()
                ))
                .collect(Collectors.toList());
    }

    public List<com.acp.simccs.modules.communication.dto.ChannelDTO> getChannels() {
        return channelRepository.findAll().stream()
                .map(channel -> new com.acp.simccs.modules.communication.dto.ChannelDTO(channel.getId(), channel.getName(), channel.getDescription()))
                .collect(Collectors.toList());
    }

    public com.acp.simccs.modules.communication.dto.ChannelDTO createChannel(com.acp.simccs.modules.communication.dto.ChannelDTO channelDTO) {
        ChatChannel channel = new ChatChannel();
        channel.setName(channelDTO.getName());
        channel.setDescription(channelDTO.getDescription());
        ChatChannel saved = channelRepository.save(channel);
        return new com.acp.simccs.modules.communication.dto.ChannelDTO(saved.getId(), saved.getName(), saved.getDescription());
    }
}