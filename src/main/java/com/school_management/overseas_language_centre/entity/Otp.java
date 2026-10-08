package com.school_management.overseas_language_centre.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "otp")
@Data
public class Otp {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private Long userId;
    private String otpEncrypted;
    private LocalDateTime expiresAt;
    private Integer sentCount = 0;
    private LocalDateTime lastSentAt;
    private Boolean verified = false;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
