package com.acp.simccs.modules.communication.controller;

import com.acp.simccs.common.dto.ResponseDTO;
import com.acp.simccs.modules.communication.dto.ChannelDTO;
import com.acp.simccs.modules.communication.dto.MessageDTO;
import com.acp.simccs.modules.communication.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    // WebSocket Endpoint (Keeps raw return for STOMP)
    @MessageMapping("/chat/{channelId}")
    @SendTo("/topic/channel/{channelId}")
    public MessageDTO sendMessage(@Payload MessageDTO messageDto,
                                  @DestinationVariable Long channelId,
                                  SimpMessageHeaderAccessor headerAccessor) {
        String senderEmail = "journalist@acp.com";
        if(headerAccessor.getUser() != null) {
            senderEmail = headerAccessor.getUser().getName();
        }
        messageDto.setChannelId(channelId);
        return chatService.saveMessage(messageDto, senderEmail);
    }

    // REST Endpoints (Fixed Generics)
    @GetMapping("/api/chat/channels/{channelId}/history")
    public ResponseEntity<ResponseDTO<List<MessageDTO>>> getChatHistory(@PathVariable Long channelId) {
        return ResponseDTO.success(chatService.getHistory(channelId)).toResponseEntity();
    }

    @GetMapping("/api/chat/channels")
    public ResponseEntity<ResponseDTO<List<ChannelDTO>>> getChannels() {
        return ResponseDTO.success(chatService.getChannels()).toResponseEntity();
    }

    @PostMapping("/api/chat/channels")
    public ResponseEntity<ResponseDTO<ChannelDTO>> createChannel(@RequestBody ChannelDTO channelDTO) {
        return ResponseDTO.success(chatService.createChannel(channelDTO)).toResponseEntity();
    }
}