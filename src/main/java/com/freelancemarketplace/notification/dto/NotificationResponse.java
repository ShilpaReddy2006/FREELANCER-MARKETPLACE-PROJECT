package com.freelancemarketplace.notification.dto;

import java.time.LocalDateTime;

public class NotificationResponse {

    private Long id;
    private Long recipientId;
    private String title;
    private String message;
    private String type;
    private boolean read;
    private LocalDateTime createdAt;

    public NotificationResponse(
            Long id,
            Long recipientId,
            String title,
            String message,
            String type,
            boolean read,
            LocalDateTime createdAt) {

        this.id = id;
        this.recipientId = recipientId;
        this.title = title;
        this.message = message;
        this.type = type;
        this.read = read;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public Long getRecipientId() {
        return recipientId;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }

    public String getType() {
        return type;
    }

    public boolean isRead() {
        return read;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}