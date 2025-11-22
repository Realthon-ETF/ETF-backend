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

        // 인증 제외 경로 & CORS preflight 는 바로 패스
        if (path.startsWith("/auth/") || "OPTIONS".equalsIgnoreCase(request.getMethod())) {
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

        // 헤더 추출
        String header = request.getHeader("Authorization");
        if (!StringUtils.hasText(header) || !header.startsWith("Bearer ")) {
            log.debug("[JWT] No Bearer token. path={}", path);
            chain.doFilter(request, response);
            return;
        }

        String token = header.substring(7);
        log.debug("[JWT] Bearer token detected. prefix={}", token.length() > 10 ? token.substring(0, 10) + "..." : token);

        // 토큰 유형/유효성 체크
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

        // 클레임에서 subject/role 추출
        String username = jwtUtil.getSubject(token);
        String domainRole = jwtUtil.getRole(token);
        if (!StringUtils.hasText(username) || !StringUtils.hasText(domainRole)) {
            log.warn("[JWT] Missing subject/role in token. path={}", path);
            chain.doFilter(request, response);
            return;
        }

        String authority = "ROLE_" + domainRole.trim().toUpperCase();
        var userDetails = org.springframework.security.core.userdetails.User
                .withUsername(username)
                .password("") // 자격증명은 불필요
                .authorities(authority)
                .build();

        // SecurityContext 설정
        var authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        log.debug("[JWT] SecurityContext set. user={}, authorities={}, path={}",
                username, userDetails.getAuthorities(), path);

        chain.doFilter(request, response);
    }
}

