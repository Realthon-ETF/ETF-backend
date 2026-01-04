package com.realthon.etf.auth.controller;

import com.realthon.etf.auth.dto.RefreshTokenRequest;
import com.realthon.etf.auth.dto.TokenResponse;
import com.realthon.etf.auth.service.AuthTokenService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthTokenService authTokenService;

    @PostMapping("/refresh")
    public void reissue(@RequestBody RefreshTokenRequest request, HttpServletResponse response) throws IOException {

        TokenResponse tokenResponse = authTokenService.reissueTokens(request.getRefreshToken());

        // 헤더 + 바디 둘 다 내려줌
        response.setHeader("Authorization", "Bearer " + tokenResponse.getAccessToken());
        response.setHeader("X-Refresh-Token", tokenResponse.getRefreshToken());
        response.setHeader("Access-Control-Expose-Headers", "Authorization,X-Refresh-Token");
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("""
            {"accessToken":"%s","refreshToken":"%s"}
        """.formatted(tokenResponse.getAccessToken(), tokenResponse.getRefreshToken()));
        response.getWriter().flush();
    }
}
