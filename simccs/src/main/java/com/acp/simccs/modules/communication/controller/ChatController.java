package com.acp.simccs.modules.communication.controller;

import com.acp.simccs.modules.communication.dto.MessageDTO;
import com.acp.simccs.modules.communication.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

//import java.security.Principal;
import com.acp.simccs.modules.communication.dto.ChannelDTO;
import java.util.List;

@Controller
@ResponseBody
public class ChatController {

    @Autowired
    private ChatService chatService;

    /**
     * WebSocket Endpoint: /app/chat/{channelId}
     * Broadcasts to: /topic/channel/{channelId}
     */
    @MessageMapping("/chat/{channelId}")
    @SendTo("/topic/channel/{channelId}")
    public MessageDTO sendMessage(@Payload MessageDTO messageDto, 
                                  @DestinationVariable Long channelId,
                                  SimpMessageHeaderAccessor headerAccessor) {
        
        // Extract user from WebSocket session (Requires JWT Handshake Interceptor for production)
        // For this phase, we assume the DTO sends the senderName, or we mock it.
        // In a real robust app, use: Principal user = headerAccessor.getUser();
        
        // Mocking sender for simplicity if Principal is null in basic tests
        String senderEmail = "journalist@acp.com"; // Replace with Principal logic
        if(headerAccessor.getUser() != null) {
             senderEmail = headerAccessor.getUser().getName();
        }

        messageDto.setChannelId(channelId);
        return chatService.saveMessage(messageDto, senderEmail);
    }

    /**
     * REST Endpoint to load chat history
     */
    /**
     * REST Endpoint to load chat history
     */
    @GetMapping("/api/chat/channels/{channelId}/history")
    public ResponseEntity<List<MessageDTO>> getChatHistory(@PathVariable Long channelId) {
        return ResponseEntity.ok(chatService.getHistory(channelId));
    }

    @GetMapping("/api/chat/channels")
    public ResponseEntity<List<ChannelDTO>> getChannels() {
        return ResponseEntity.ok(chatService.getChannels());
    }

    @PostMapping("/api/chat/channels")
    public ResponseEntity<ChannelDTO> createChannel(@RequestBody ChannelDTO channelDTO) {
        return ResponseEntity.ok(chatService.createChannel(channelDTO));
    }
    
}