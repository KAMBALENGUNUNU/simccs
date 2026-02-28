package com.acp.simccs.modules.communication.controller;

import com.acp.simccs.common.dto.ResponseDTO;
import com.acp.simccs.modules.communication.dto.ChannelDTO;
import com.acp.simccs.modules.communication.dto.MessageDTO;
import com.acp.simccs.modules.communication.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.http.MediaType;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    // SSE Streaming Endpoint
    @GetMapping(value = "/api/chat/channels/{channelId}/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamMessages(@PathVariable Long channelId) {
        return chatService.subscribeToChannel(channelId);
    }

    // REST Endpoint for sending messages
    @PostMapping("/api/chat/channels/{channelId}/messages")
    public ResponseEntity<ResponseDTO<MessageDTO>> sendMessage(
            @PathVariable Long channelId,
            @RequestBody MessageDTO messageDto) {

        String senderEmail = "journalist@acp.com";
        var authentication = org.springframework.security.core.context.SecurityContextHolder.getContext()
                .getAuthentication();
        if (authentication != null && authentication.isAuthenticated()
                && !authentication.getName().equals("anonymousUser")) {
            senderEmail = authentication.getName();
        }

        messageDto.setChannelId(channelId);
        MessageDTO savedMessage = chatService.saveMessage(messageDto, senderEmail);
        return ResponseDTO.success(savedMessage).toResponseEntity();
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

    @GetMapping("/api/chat/reports/{reportId}/channel")
    public ResponseEntity<ResponseDTO<ChannelDTO>> getReportChannel(@PathVariable Long reportId) {
        return ResponseDTO.success(chatService.getReportChannel(reportId)).toResponseEntity();
    }
}