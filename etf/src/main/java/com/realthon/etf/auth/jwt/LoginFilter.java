package com.realthon.etf.auth.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.realthon.etf.auth.dto.CustomUserDetails;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.core.Authentication;

import java.io.IOException;

public class LoginFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public LoginFilter(AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        setFilterProcessesUrl("/auth/login"); // 로그인 URL
        setAuthenticationManager(authenticationManager);

        setUsernameParameter("loginId");
        setPasswordParameter("password");
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {

        String loginId, password;
        try {
            String ct = request.getContentType();
            if (ct != null && ct.toLowerCase().startsWith("application/json")) {
                var node = new ObjectMapper().readTree(request.getInputStream());

                var idNode = node.get("loginId");
                var pwNode = node.get("password");
                if (idNode == null || pwNode == null) {
                    throw new AuthenticationServiceException("Missing loginId or password in JSON body");
                }

                loginId  = idNode.asText();
                password = pwNode.asText();
            } else {
                loginId  = obtainUsername(request); // setUsernameParameter("loginId") 적용됨
                password = obtainPassword(request);
                if (loginId == null || password == null) {
                    throw new AuthenticationServiceException("Missing loginId or password in form parameters");
                }
            }
        } catch (IOException e) {
            throw new AuthenticationServiceException("Invalid login payload", e);
        }

        return authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginId, password)
        );
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                            FilterChain chain, Authentication authentication) throws IOException {
        CustomUserDetails principal = (CustomUserDetails) authentication.getPrincipal();
        String username = principal.getUsername(); // 내부적으로 loginId 반환하도록 구현되어 있어야 함

        String domainRole = principal.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)            // ex) ROLE_ADMIN
                .map(a -> a.startsWith("ROLE_") ? a.substring(5) : a) // -> ADMIN
                .findFirst().orElse("EXTERNAL");

        String accessToken  = jwtUtil.createAccessToken(username, domainRole);
        String refreshToken = jwtUtil.createRefreshToken(username);

        response.setHeader("Authorization", "Bearer " + accessToken);
        response.setHeader("X-Refresh-Token", refreshToken);
        response.setHeader("Access-Control-Expose-Headers", "Authorization,X-Refresh-Token");
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("""
            {"accessToken":"%s","refreshToken":"%s"}
        """.formatted(accessToken, refreshToken));
        response.getWriter().flush();
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                              AuthenticationException failed) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"error\":\"invalid_credentials\"}");
        response.getWriter().flush();
    }
}


