package com.acp.simccs.modules.identity.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Autowired(required = false)
    private JavaMailSender emailSender;

    public void sendSimpleMessage(String to, String subject, String text) {
        if (emailSender != null) {
            try {
                SimpleMailMessage message = new SimpleMailMessage();
                message.setFrom("noreply@simccs.com");
                message.setTo(to);
                message.setSubject(subject);
                message.setText(text);
                emailSender.send(message);
            } catch (Exception e) {
                logger.error("Failed to send email to {}: {}", to, e.getMessage());
                // Fallback to logging
                logger.info("EMAIL TO: {}\nSUBJECT: {}\nTEXT: {}", to, subject, text);
            }
        } else {
            logger.warn("JavaMailSender not configured. Logging email instead.");
            logger.info("EMAIL TO: {}\nSUBJECT: {}\nTEXT: {}", to, subject, text);
        }
    }
}
