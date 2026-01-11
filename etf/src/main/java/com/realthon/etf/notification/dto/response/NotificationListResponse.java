package com.realthon.etf.notification.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationListResponse {

    private long totalCount;
    private List<NotificationResponse> notifications;

    public static NotificationListResponse of(long totalCount, List<NotificationResponse> notifications) {
        return NotificationListResponse.builder()
                .totalCount(totalCount)
                .notifications(notifications)
                .build();
    }
}
