package com.acp.simccs.modules.communication.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageDTO {
    private String senderName;
    private String content; // Plain text (will be encrypted by service)
    private Long channelId;
    private String timestamp;
}