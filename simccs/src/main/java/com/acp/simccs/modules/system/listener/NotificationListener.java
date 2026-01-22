package com.acp.simccs.modules.system.listener;

import com.acp.simccs.modules.crisis.event.ReportSubmittedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class NotificationListener {

    private static final Logger logger = LoggerFactory.getLogger(NotificationListener.class);

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Async
    @EventListener
    public void handleReportSubmittedEvent(ReportSubmittedEvent event) {
        logger.info("Received event: New Report Submitted with ID: " + event.getReport().getId());

        // Construct a simple alert message
        String alertMessage = "URGENT: New Report from " + 
                              event.getReport().getAuthor().getFullName() + 
                              " in region " + event.getReport().getLocationLat();

        // Push to a public dashboard topic
        messagingTemplate.convertAndSend("/topic/alerts", alertMessage);
    }
}