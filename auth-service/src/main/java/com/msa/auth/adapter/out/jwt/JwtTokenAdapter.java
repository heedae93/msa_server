package com.msa.auth.adapter.out.jwt;

import com.msa.auth.application.port.out.JwtTokenValidatorPort;
import com.msa.auth.domain.UserRole;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Component
public class JwtTokenAdapter implements JwtTokenValidatorPort {

    // 🚨 application.yml 또는 Config Server에서 설정된 secret-key를 주입받습니다.
    @Value("${service.jwt.secret-key}")
    private String secretKey;

    private Key key;

    @PostConstruct
    public void init() {
        // Base64로 인코딩된 문자열을 Key 객체로 변환
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    // 🚨 이전 AuthService에서 구현했던 createToken 메서드도 여기에 위치하는 것이 더 깔끔합니다.
    // (TokenProviderPort 구현체 역할을 겸합니다.)

    // ... (이전에 구현했던 createToken 메서드를 여기에 옮겨주세요.) ...


    @Override
    public Authentication validateToken(String token) {
        Claims claims = getClaims(token);

        // 1. 토큰 만료 여부 검사
        if (claims.getExpiration().before(new Date())) {
            throw new RuntimeException("만료된 토큰입니다.");
        }

        // 2. 권한 정보 추출
        String email = claims.getSubject();
        String roleName = claims.get("role", String.class);
        UserRole role = UserRole.valueOf(roleName);

        List<GrantedAuthority> authorities = Collections.singletonList(
                new SimpleGrantedAuthority(role.name())
        );

        // 3. Spring Security의 인증 객체 (Authentication) 생성
        return new UsernamePasswordAuthenticationToken(email, null, authorities);
    }

    // JWT 토큰 파싱(해석) 전용 내부 메서드
    private Claims getClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key) // Secret Key를 사용해서 서명 검증
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            // 토큰 형식 오류, 서명 불일치 등 모든 실패는 RuntimeException으로 처리
            throw new RuntimeException("유효하지 않은 토큰입니다.", e);
        }
    }
}
