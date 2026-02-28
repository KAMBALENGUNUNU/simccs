package com.acp.simccs.modules.communication.service;

import com.acp.simccs.modules.communication.dto.MessageDTO;
import com.acp.simccs.modules.communication.model.ChatChannel;
import com.acp.simccs.modules.communication.model.ChatMessage;
import com.acp.simccs.modules.communication.repository.ChatChannelRepository;
import com.acp.simccs.modules.communication.repository.ChatMessageRepository;
import com.acp.simccs.modules.identity.model.User;
import com.acp.simccs.modules.identity.repository.UserRepository;
import com.acp.simccs.modules.crisis.repository.CrisisReportRepository;
import com.acp.simccs.security.SecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
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
        private CrisisReportRepository crisisReportRepository;
        @Autowired
        private SecurityService securityService;

        private final Map<Long, CopyOnWriteArrayList<SseEmitter>> emitters = new ConcurrentHashMap<>();

        public SseEmitter subscribeToChannel(Long channelId) {
                SseEmitter emitter = new SseEmitter(3600000L); // 1 hour timeout
                emitters.computeIfAbsent(channelId, id -> new CopyOnWriteArrayList<>()).add(emitter);

                emitter.onCompletion(() -> removeEmitter(channelId, emitter));
                emitter.onTimeout(() -> removeEmitter(channelId, emitter));
                emitter.onError((e) -> removeEmitter(channelId, emitter));

                // 🛑 CRITICAL FIX: Send an immediate connection event to fully open and commit the AsyncContext
                try {
                        emitter.send(SseEmitter.event().name("init").data("{\"type\":\"INIT\", \"message\":\"Connected\"}"));
                } catch (IOException e) {
                        removeEmitter(channelId, emitter);
                }

                return emitter;
        }

        private void removeEmitter(Long channelId, SseEmitter emitter) {
                CopyOnWriteArrayList<SseEmitter> channelEmitters = emitters.get(channelId);
                if (channelEmitters != null) {
                        channelEmitters.remove(emitter);
                        if (channelEmitters.isEmpty()) {
                                emitters.remove(channelId);
                        }
                }
        }

        private void broadcastMessage(Long channelId, MessageDTO messageDTO) {
                CopyOnWriteArrayList<SseEmitter> channelEmitters = emitters.get(channelId);
                if (channelEmitters != null) {
                        for (SseEmitter emitter : channelEmitters) {
                                try {
                                        emitter.send(SseEmitter.event().name("message").data(messageDTO));
                                } catch (Exception e) {
                                        // 🛑 THE FIX: Do NOT call emitter.completeWithError(e) here!
                                        // If sending fails, the connection is already dead.
                                        // Attempting to complete it triggers the AsyncContext crash.
                                        // Just cleanly remove it from our active list.
                                        removeEmitter(channelId, emitter);
                                }
                        }
                }
        }

        @Transactional
        public MessageDTO saveMessage(MessageDTO messageDto, String senderEmail) {
                User sender = userRepository.findByEmail(senderEmail)
                        .orElseThrow(() -> new RuntimeException("User not found"));

                ChatChannel channel = channelRepository.findById(messageDto.getChannelId())
                        .orElseThrow(() -> new RuntimeException("Channel not found"));

                ChatMessage message = new ChatMessage();
                message.setSender(sender);
                message.setChannel(channel);

                // Encrypt using the SecurityService from security package
                message.setContentEncrypted(securityService.encrypt(messageDto.getContent()));

                ChatMessage savedMsg = messageRepository.save(message);

                MessageDTO responseMessage = new MessageDTO(
                        sender.getFullName(),
                        messageDto.getContent(),
                        channel.getId(),
                        savedMsg.getSentAt().toString());

                broadcastMessage(channel.getId(), responseMessage);
                return responseMessage;
        }

        public List<MessageDTO> getHistory(Long channelId) {
                return messageRepository.findByChannelIdOrderBySentAtAsc(channelId).stream()
                        .map(msg -> new MessageDTO(
                                msg.getSender().getFullName(),
                                securityService.decrypt(msg.getContentEncrypted()),
                                msg.getChannel().getId(),
                                msg.getSentAt().toString()))
                        .collect(Collectors.toList());
        }

        public List<com.acp.simccs.modules.communication.dto.ChannelDTO> getChannels() {
                return channelRepository.findAll().stream()
                        .map(channel -> new com.acp.simccs.modules.communication.dto.ChannelDTO(channel.getId(),
                                channel.getName(), channel.getDescription()))
                        .collect(Collectors.toList());
        }

        public com.acp.simccs.modules.communication.dto.ChannelDTO createChannel(
                com.acp.simccs.modules.communication.dto.ChannelDTO channelDTO) {
                ChatChannel channel = new ChatChannel();
                channel.setName(channelDTO.getName());
                channel.setDescription(channelDTO.getDescription());
                ChatChannel saved = channelRepository.save(channel);
                return new com.acp.simccs.modules.communication.dto.ChannelDTO(saved.getId(), saved.getName(),
                        saved.getDescription());
        }

        @Transactional
        public com.acp.simccs.modules.communication.dto.ChannelDTO createReportChannel(Long reportId,
                                                                                       String reportTitle) {
                ChatChannel channel = new ChatChannel();
                channel.setName("report-" + reportId);
                channel.setType("REPORT");
                channel.setDescription("Discussion for: " + reportTitle);
                channel.setReportId(reportId);
                ChatChannel saved = channelRepository.save(channel);
                return new com.acp.simccs.modules.communication.dto.ChannelDTO(saved.getId(), saved.getName(),
                        saved.getDescription());
        }

        @Transactional
        public com.acp.simccs.modules.communication.dto.ChannelDTO getReportChannel(Long reportId) {
                return channelRepository.findByReportId(reportId)
                        .map(channel -> new com.acp.simccs.modules.communication.dto.ChannelDTO(
                                channel.getId(), channel.getName(), channel.getDescription()))
                        .orElseGet(() -> {
                                com.acp.simccs.modules.crisis.model.CrisisReport report = crisisReportRepository
                                        .findById(reportId)
                                        .orElseThrow(() -> new RuntimeException(
                                                "Report not found: " + reportId));
                                return createReportChannel(reportId, report.getTitle());
                        });
        }
}