package com.realthon.etf.auth.service;

import com.realthon.etf.auth.dto.CustomUserDetails;
import com.realthon.etf.auth.dto.TokenResponse;
import com.realthon.etf.auth.jwt.JwtUtil;
import com.realthon.etf.global.exception.CustomException;
import com.realthon.etf.global.exception.ExceptionCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthTokenService {

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService customUserDetailsService;

    public TokenResponse reissueTokens(String refreshToken) {

        try {
            // parseClaims 안에서 서명 검증/만료 체크 중 예외 발생 가능
            jwtUtil.parseClaims(refreshToken);
        } catch (Exception e) {
            throw new CustomException(ExceptionCode.AUTH_TOKEN_INVALID);
        }
        if (!jwtUtil.isRefreshToken(refreshToken)) {
            throw new CustomException(ExceptionCode.REFRESH_TOKEN_EMPTY);
        }
        if (jwtUtil.isExpired(refreshToken)) {
            throw new CustomException(ExceptionCode.AUTH_TOKEN_EXPIRED);
        }

        // subject = username(loginId) 꺼내기
        String username = jwtUtil.getSubject(refreshToken);

        CustomUserDetails userDetails =
                (CustomUserDetails) customUserDetailsService.loadUserByUsername(username);
        if (userDetails == null) {
            throw new UsernameNotFoundException("사용자를 찾을 수 없음");
        }

        // 새 토큰 발급
        String newAccessToken  = jwtUtil.createAccessToken(username);
        String newRefreshToken = jwtUtil.createRefreshToken(username);

        return new TokenResponse(newAccessToken, newRefreshToken);
    }
}

