package com.realthon.etf.notification.controller;

import com.realthon.etf.notification.dto.NotificationListResponse;
import com.realthon.etf.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    /**
     * 알림 목록 조회
     */
    @GetMapping
    public ResponseEntity<NotificationListResponse> getNotifications() {
        NotificationListResponse response = notificationService.getNotificationList();
        return ResponseEntity.ok(response);
    }
}
