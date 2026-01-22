package com.acp.simccs.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
public class SchedulerConfig {
    // Scheduling is enabled. Specific schedules will be defined in Service classes.
}