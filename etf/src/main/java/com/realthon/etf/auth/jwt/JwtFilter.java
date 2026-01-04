package com.realthon.etf.auth.jwt;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Slf4j
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    public JwtFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
            throws ServletException, IOException {

        final String path = request.getServletPath();
        log.debug("[JWT] Enter filter. method={}, path={}", request.getMethod(), path);

        // 토큰 검사에서 제외할 공개 경로
        boolean isPublicAuthPath =
                path.equals("/auth/login") ||
                        path.equals("/auth/refresh") ||
                        path.startsWith("/auth/signup");

        if (isPublicAuthPath || "OPTIONS".equalsIgnoreCase(request.getMethod())) {
            log.debug("[JWT] Skip path or preflight: {} {}", request.getMethod(), path);
            chain.doFilter(request, response);
            return;
        }

        // 이미 인증된 경우 패스
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            log.debug("[JWT] SecurityContext already set. path={}", path);
            chain.doFilter(request, response);
            return;
        }

        // Authorization: Bearer xxx
        String header = request.getHeader("Authorization");
        if (!StringUtils.hasText(header) || !header.startsWith("Bearer ")) {
            log.debug("[JWT] No Bearer token. path={}", path);
            chain.doFilter(request, response);
            return;
        }

        String token = header.substring(7);
        log.debug("[JWT] Bearer token detected. prefix={}",
                token.length() > 10 ? token.substring(0, 10) + "..." : token);

        // 토큰 유효성 체크
        try {
            // refresh 토큰이면 거부 (access 만 허용)
            String typ = jwtUtil.getType(token);
            if ("refresh".equalsIgnoreCase(typ)) {
                log.warn("[JWT] Refresh token used for API. path={}", path);
                chain.doFilter(request, response);
                return;
            }

            if (jwtUtil.isExpired(token)) {
                log.warn("[JWT] Token expired. path={}", path);
                chain.doFilter(request, response);
                return;
            }

        } catch (JwtException e) {
            log.warn("[JWT] Token invalid: {} (path={})", e.getMessage(), path);
            chain.doFilter(request, response);
            return;
        } catch (Exception e) {
            log.error("[JWT] Validation error: {} (path={})", e.getMessage(), path);
            chain.doFilter(request, response);
            return;
        }

        // subject 사용
        String username = jwtUtil.getSubject(token);
        if (!StringUtils.hasText(username)) {
            log.warn("[JWT] Missing subject in token. path={}", path);
            chain.doFilter(request, response);
            return;
        }

        // 권한 없으면 빈 리스트로 세팅 가능
        var userDetails = org.springframework.security.core.userdetails.User
                .withUsername(username)
                .password("")
                .authorities(List.of())
                .build();

        var authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        log.debug("[JWT] SecurityContext set. user={}, path={}", username, path);

        chain.doFilter(request, response);
    }
}
