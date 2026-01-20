package com.realthon.etf.ai.dto.request;

import com.realthon.etf.user.dto.response.UserResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AiCrawlRequest {

    private Long userId;
    private String requestId;
    private String targetUrl;
    private UserResponse userProfile;
    private CallbackDto callback;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CallbackDto {
        private boolean enabled;
        private String callbackUrl;
        private String authToken;
    }
}
