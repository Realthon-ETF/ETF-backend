package com.realthon.etf.auth.jwt;

import com.realthon.etf.auth.dto.CustomUserDetails;
import com.realthon.etf.user.domain.User;
import com.realthon.etf.user.repository.UserRepository;
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
    private final UserRepository userRepository;

    public JwtFilter(JwtUtil jwtUtil, UserRepository userRepository) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
            throws ServletException, IOException {

        final String path = request.getServletPath();
        log.debug("[JWT] Enter filter. method={}, path={}", request.getMethod(), path);

        boolean isPublicAuthPath =
                        path.equals("/auth/login") ||
                        path.equals("/auth/refresh") ||
                        path.startsWith("/auth/signup");

        if (isPublicAuthPath || "OPTIONS".equalsIgnoreCase(request.getMethod())) {
            chain.doFilter(request, response);
            return;
        }

        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            chain.doFilter(request, response);
            return;
        }

        String header = request.getHeader("Authorization");
        if (!StringUtils.hasText(header) || !header.startsWith("Bearer ")) {
            chain.doFilter(request, response);
            return;
        }

        String token = header.substring(7);

        try {
            String typ = jwtUtil.getType(token);
            if ("refresh".equalsIgnoreCase(typ)) {
                chain.doFilter(request, response);
                return;
            }
            if (jwtUtil.isExpired(token)) {
                chain.doFilter(request, response);
                return;
            }
        } catch (JwtException e) {
            chain.doFilter(request, response);
            return;
        } catch (Exception e) {
            chain.doFilter(request, response);
            return;
        }

        String loginId = jwtUtil.getSubject(token);
        if (!StringUtils.hasText(loginId)) {
            chain.doFilter(request, response);
            return;
        }

        User user = userRepository.findByLoginId(loginId)
                .orElse(null);

        if (user == null) {
            // 토큰은 있는데 사용자가 없으면 인증 세팅하지 않고 통과(혹은 401 처리)
            log.warn("[JWT] User not found for subject={}", loginId);
            chain.doFilter(request, response);
            return;
        }

        CustomUserDetails customUserDetails = new CustomUserDetails(user);

        var authentication = new UsernamePasswordAuthenticationToken(
                customUserDetails,
                null,
                customUserDetails.getAuthorities()
        );
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        log.debug("[JWT] SecurityContext set. loginId={}, userId={}, path={}",
                loginId, customUserDetails.getUserId(), path);

        chain.doFilter(request, response);
    }
}
