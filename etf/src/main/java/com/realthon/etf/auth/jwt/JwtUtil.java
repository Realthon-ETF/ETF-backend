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

    public JwtUtil(
            @Value("${spring.jwt.secret}") String secretBase64,
            @Value("${spring.jwt.access-ttl:900000}") long accessTtlMillis,
            @Value("${spring.jwt.refresh-ttl:604800000}") long refreshTtlMillis
    ) {
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretBase64.trim().replaceAll("\\s", "")));
        this.parser = Jwts.parserBuilder().setSigningKey(this.key).build();
        this.accessTtlMillis = accessTtlMillis;
        this.refreshTtlMillis = refreshTtlMillis;
    }

    public String createAccessToken(String loginId) {
        return buildToken(loginId, "access", accessTtlMillis);
    }

    public String createRefreshToken(String loginId) {
        return buildToken(loginId, "refresh", refreshTtlMillis);
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
        String typ = getType(token);
        return "refresh".equals(typ);
    }
}

