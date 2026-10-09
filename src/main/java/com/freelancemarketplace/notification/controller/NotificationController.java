package com.freelancemarketplace.notification.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.freelancemarketplace.notification.dto.NotificationResponse;
import com.freelancemarketplace.notification.service.NotificationService;

@RestController
@RequestMapping("/api/notifications")
@PreAuthorize("hasAnyRole('CLIENT', 'FREELANCER')")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(
            NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    // GET all notifications for the logged-in user.
    @GetMapping("/my")
    public ResponseEntity<List<NotificationResponse>> getMyNotifications(
            Authentication authentication) {

        Long userId = (Long) authentication.getPrincipal();

        return ResponseEntity.ok(
            notificationService.getMyNotifications(userId)
        );
    }

    // GET unread notifications.
    @GetMapping("/unread")
    public ResponseEntity<List<NotificationResponse>> getUnreadNotifications(
            Authentication authentication) {

        Long userId = (Long) authentication.getPrincipal();

        return ResponseEntity.ok(
            notificationService.getUnreadNotifications(userId)
        );
    }

    // GET unread notification count.
    @GetMapping("/unread/count")
    public ResponseEntity<Long> getUnreadCount(
            Authentication authentication) {

        Long userId = (Long) authentication.getPrincipal();

        return ResponseEntity.ok(
            notificationService.getUnreadCount(userId)
        );
    }

    // PUT to mark a notification as read.
    @PutMapping("/{notificationId}/read")
    public ResponseEntity<NotificationResponse> markAsRead(
            @PathVariable Long notificationId,
            Authentication authentication) {

        Long userId = (Long) authentication.getPrincipal();

        return ResponseEntity.ok(
            notificationService.markAsRead(notificationId, userId)
        );
    }
}