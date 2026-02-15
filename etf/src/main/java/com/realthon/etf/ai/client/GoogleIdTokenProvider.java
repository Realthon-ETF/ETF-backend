package com.realthon.etf.ai.client;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.IdTokenCredentials;
import com.google.auth.oauth2.IdTokenProvider;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class GoogleIdTokenProvider {

    public String getIdToken(String audience) {
        try {
            // GCP 환경(Cloud Run 등)에서는 자동으로 서비스 계정 권한을 가져옴
            GoogleCredentials credentials = GoogleCredentials.getApplicationDefault();

            if (credentials instanceof IdTokenProvider) {
                IdTokenCredentials idTokenCredentials = IdTokenCredentials.newBuilder()
                        .setIdTokenProvider((IdTokenProvider) credentials)
                        .setTargetAudience(audience) // 호출할 서비스의 URL
                        .build();

                // 유효한 토큰 반환 (필요시 자동 갱신)
                return idTokenCredentials.refreshAccessToken().getTokenValue();
            }
            throw new RuntimeException("현재 환경에서 ID 토큰을 생성할 수 없습니다.");
        } catch (IOException e) {
            throw new RuntimeException("Google ID Token 발급 실패", e);
        }
    }
}
