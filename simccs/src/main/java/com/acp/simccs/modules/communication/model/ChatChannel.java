package com.acp.simccs.modules.communication.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "channels")
@Data
@NoArgsConstructor
public class ChatChannel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    // e.g., PUBLIC, PRIVATE, DIRECT
    @Column(nullable = false)
    private String type;

    private String description; 

    private LocalDateTime createdAt = LocalDateTime.now();

    public ChatChannel(String name, String type) {
        this.name = name;
        this.type = type;
    }
}