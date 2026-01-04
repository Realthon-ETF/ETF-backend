package com.realthon.etf.auth.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.Map;

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

    public String createAccessToken(String username, String domainRole) {
        return buildToken(username, Map.of("role", domainRole, "typ", "access"), accessTtlMillis);
    }

    public String createRefreshToken(String username) {
        return buildToken(username, Map.of("typ", "refresh"), refreshTtlMillis);
    }

    private String buildToken(String subject, Map<String, Object> claims, long ttlMillis) {
        Date now = new Date();
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + ttlMillis))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // 조회/검증
    public Claims parseClaims(String token) { return parser.parseClaimsJws(token).getBody(); }
    public String getSubject(String token) { return parseClaims(token).getSubject(); }
    public String getRole(String token)    { return parseClaims(token).get("role", String.class); }
    public boolean isExpired(String token) { return parseClaims(token).getExpiration().before(new Date()); }
    public String getType(String token)    { return parseClaims(token).get("typ", String.class); }
}

