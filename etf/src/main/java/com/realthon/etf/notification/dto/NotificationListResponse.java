package com.realthon.etf.notification.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class NotificationListResponse {

    private List<NotificationResponse> notifications;

    public static NotificationListResponse of(List<NotificationResponse> notifications) {
        return NotificationListResponse.builder()
                .notifications(notifications)
                .build();
    }
}
