package com.realthon.etf.ai.dto.request;

import lombok.*;

import java.util.Set;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AiCrawlRequest {

    private String userId;
    private String targetUrl;
    private UserProfile userProfile;
    private String summary;

    private CallbackDto callback;

    // =========================
    // 내부 DTO
    // =========================

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UserProfile {

        private String username;
        private String phoneNumber;
        private String school;
        private String major;
        private Set<String> interestFields;
        private Long intervalDays;
        private String alarmTime;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CallbackDto {

        // 콜백 사용 여부
        private boolean enabled;
        // AI 작업 완료 후 호출할 백엔드 URL
        private String callbackUrl;
        // 콜백 인증 토큰
        private String authToken;
    }
}
