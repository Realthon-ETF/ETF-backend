package com.realthon.etf.notification.dto.response;

import com.realthon.etf.notification.domain.Notification;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationLikeResponse{

    private Long notificationId;
    private boolean isLiked;

    public static NotificationLikeResponse from(Notification notification) {
        return NotificationLikeResponse.builder()
                .notificationId(notification.getNotificationId())
                .isLiked(notification.isLiked())
                .build();
    }
}