package com.realthon.etf.notification.dto;

import com.realthon.etf.notification.domain.Notification;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class NotificationResponse {

    private Long id;
    private String title;
    private String content;
    private String noticeUrl;      // 공지 URL
    private LocalDateTime createdAt;

    public static NotificationResponse from(Notification notification) {
        return NotificationResponse.builder()
                .id(notification.getId())
                .title(notification.getTitle())
                .content(notification.getContent())
                .noticeUrl(notification.getNoticeUrl())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}
