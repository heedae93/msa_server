package com.msa.auth.adapter.out.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    private final Key key;
    private final long expiration;

    // 생성자: 설정 파일에서 비밀키와 만료 시간을 가져온다.
    public JwtUtil(@Value("${service.jwt.secret-key}") String secretKey,
                   @Value("${service.jwt.expiration}") long expiration) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.expiration = expiration;
    }

    // 🏭 토큰 생성 메서드
    public String createToken(String userId, String role) {
        return Jwts.builder()
                .setSubject(userId) // 토큰 주인 (ID)
                .claim("role", role) // 추가 정보 (권한 등)
                .setIssuedAt(new Date()) // 발행 시간
                .setExpiration(new Date(System.currentTimeMillis() + expiration)) // 만료 시간
                .signWith(key, SignatureAlgorithm.HS256) // 비밀키로 서명 (도장 쾅!)
                .compact();
    }
}
