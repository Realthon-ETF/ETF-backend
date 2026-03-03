package com.realthon.etf.notification.dto.response;

import com.realthon.etf.notification.domain.Notification;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationResponse {

    private Long notificationId;
    private String category;
    private String title;
    private String sourceName;
    private String summary;
    private String originalUrl;
    private LocalDateTime createdAt;
    private boolean isLiked;

    public static NotificationResponse from(Notification notification) {
        return NotificationResponse.builder()
                .notificationId(notification.getNotificationId())
                .category(notification.getCategory())
                .title(notification.getTitle())
                .sourceName(notification.getSourceName())
                .summary(notification.getSummary())
                .originalUrl(notification.getOriginalUrl())
                .createdAt(notification.getCreatedAt())
                .isLiked(notification.isLiked())
                .build();
    }
}
