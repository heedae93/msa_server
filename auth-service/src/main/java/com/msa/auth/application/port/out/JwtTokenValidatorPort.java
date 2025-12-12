package com.msa.auth.application.port.out;

import org.springframework.security.core.Authentication;

// JWT 토큰의 유효성을 검사하고 인증 객체를 반환하는 포트
public interface JwtTokenValidatorPort {

    /**
     * JWT 토큰을 받아서 유효성을 검증하고, Spring Security의 인증(Authentication) 객체를 생성합니다.
     * @param token JWT 문자열
     * @return 인증 객체 (UsernamePasswordAuthenticationToken 등)
     */
    Authentication validateToken(String token);
}
