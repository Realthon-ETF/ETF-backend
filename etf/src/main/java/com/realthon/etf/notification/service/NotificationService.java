package com.realthon.etf.notification.service;

import com.realthon.etf.notification.domain.Notification;
import com.realthon.etf.notification.dto.NotificationListResponse;
import com.realthon.etf.notification.dto.NotificationResponse;
import com.realthon.etf.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationService {

    private final NotificationRepository notificationRepository;

    /**
     * 알림 목록 조회 (최신순)
     */
    public NotificationListResponse getNotificationList() {
        List<Notification> notifications = notificationRepository.findAllByOrderByCreatedAtDesc();

        List<NotificationResponse> responses = notifications.stream()
                .map(NotificationResponse::from)
                .toList();

        return NotificationListResponse.of(responses);
    }
}
