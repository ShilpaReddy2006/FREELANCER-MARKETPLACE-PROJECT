package com.freelancemarketplace.notification.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.freelancemarketplace.exception.ForbiddenException;
import com.freelancemarketplace.exception.ResourceNotFoundException;
import com.freelancemarketplace.notification.dto.NotificationResponse;
import com.freelancemarketplace.notification.entity.Notification;
import com.freelancemarketplace.notification.repository.NotificationRepository;
import com.freelancemarketplace.user.entity.User;
import com.freelancemarketplace.user.repository.UserRepository;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public NotificationService(
            NotificationRepository notificationRepository,
            UserRepository userRepository) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }

    // Called internally when an event occurs in the application.
    @Transactional
    public NotificationResponse createNotification(
            Long recipientId,
            String title,
            String message,
            String type) {

        User recipient = userRepository.findById(recipientId)
                .orElseThrow(() ->
                    new ResourceNotFoundException("Recipient user not found"));

        Notification notification = new Notification();
        notification.setRecipient(recipient);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setType(type);
        notification.setRead(false);

        return mapToResponse(
            notificationRepository.save(notification)
        );
    }

    // Get all notifications belonging to the logged-in user.
    @Transactional(readOnly = true)
    public List<NotificationResponse> getMyNotifications(Long userId) {

        return notificationRepository
                .findByRecipient_IdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // Get only unread notifications.
    @Transactional(readOnly = true)
    public List<NotificationResponse> getUnreadNotifications(Long userId) {

        return notificationRepository
                .findByRecipient_IdAndReadFalseOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // Count unread notifications.
    @Transactional(readOnly = true)
    public long getUnreadCount(Long userId) {
        return notificationRepository.countByRecipient_IdAndReadFalse(userId);
    }

    // Mark a notification as read, after verifying ownership.
    @Transactional
    public NotificationResponse markAsRead(
            Long notificationId,
            Long userId) {

        Notification notification = notificationRepository
                .findById(notificationId)
                .orElseThrow(() ->
                    new ResourceNotFoundException("Notification not found"));

        if (!notification.getRecipient().getId().equals(userId)) {
            throw new ForbiddenException(
                "You cannot modify another user's notification");
        }

        notification.setRead(true);

        return mapToResponse(
            notificationRepository.save(notification)
        );
    }

    private NotificationResponse mapToResponse(
            Notification notification) {

        return new NotificationResponse(
            notification.getId(),
            notification.getRecipient().getId(),
            notification.getTitle(),
            notification.getMessage(),
            notification.getType(),
            notification.isRead(),
            notification.getCreatedAt()
        );
    }
}