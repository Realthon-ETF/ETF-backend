package com.realthon.etf.auth.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    private final Key key;
    private final JwtParser parser;
    private final long accessTtlMillis;
    private final long refreshTtlMillis;
    private final long resetTtlMillis;

    public JwtUtil(
            @Value("${spring.jwt.secret}") String secretBase64,
            @Value("${spring.jwt.access-ttl:900000}") long accessTtlMillis,
            @Value("${spring.jwt.refresh-ttl:604800000}") long refreshTtlMillis,
            @Value("${spring.jwt.reset-ttl:600000}") long resetTtlMillis
    ) {
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretBase64.trim().replaceAll("\\s", "")));
        this.parser = Jwts.parserBuilder().setSigningKey(this.key).build();
        this.accessTtlMillis = accessTtlMillis;
        this.refreshTtlMillis = refreshTtlMillis;
        this.resetTtlMillis = resetTtlMillis;
    }

    public String createAccessToken(String loginId) {
        return buildToken(loginId, "access", accessTtlMillis);
    }

    public String createRefreshToken(String loginId) {
        return buildToken(loginId, "refresh", refreshTtlMillis);
    }

    // Reset 전용 토큰 (subject=email)
    public String createPasswordResetToken(String email) {
        return buildToken(email, "password-reset", resetTtlMillis);
    }

    private String buildToken(String subject, String typ, long ttlMillis) {
        Date now = new Date();
        return Jwts.builder()
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + ttlMillis))
                .claim("typ", typ)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // 조회/검증
    public Claims parseClaims(String token) { return parser.parseClaimsJws(token).getBody(); }
    public String getSubject(String token) { return parseClaims(token).getSubject(); }
    public String getRole(String token)    { return parseClaims(token).get("role", String.class); }
    public boolean isExpired(String token) { return parseClaims(token).getExpiration().before(new Date()); }
    public String getType(String token)    { return parseClaims(token).get("typ", String.class); }

    public boolean isRefreshToken(String token) {
        return "refresh".equals(getType(token));
    }

    // reset 토큰 타입 검사
    public boolean isPasswordResetToken(String token) {
        return "password-reset".equals(getType(token));
    }
}