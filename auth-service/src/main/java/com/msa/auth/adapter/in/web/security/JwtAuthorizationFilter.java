package com.msa.auth.adapter.in.web.security;

import com.msa.auth.adapter.out.jwt.JwtUtil;
import com.msa.auth.application.port.out.LoadUserPort;
import com.msa.auth.domain.User;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final LoadUserPort loadUserPort; // 헥사고날: Port를 통해 도메인에 접근

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 1. 요청 헤더에서 토큰 추출
        String token = resolveToken(request);

        // 2. 토큰이 있고, 유효하다면 인증 처리
        if (token != null && jwtUtil.validateToken(token)) {
            // 토큰에서 사용자 정보(Claims) 추출
            Claims claims = jwtUtil.getUserInfoFromToken(token);
            String username = claims.getSubject();

            // DB에서 사용자 조회 (존재하는지 확인)
            loadUserPort.loadUser(username).ifPresent(user -> {
                // 인증 객체 생성 (비밀번호는 null로 설정 - 이미 토큰으로 인증됨)
                Authentication authentication = createAuthentication(user);

                // Spring Security 컨텍스트에 저장 (이제 이 요청은 인증된 상태!)
                SecurityContextHolder.getContext().setAuthentication(authentication);
            });
        }

        // 3. 다음 필터로 넘김
        filterChain.doFilter(request, response);
    }

    // 헤더에서 "Bearer "를 떼고 토큰만 가져오는 메서드
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    // 도메인 유저 -> 스프링 시큐리티 인증 객체로 변환
    private Authentication createAuthentication(User user) {
        // 권한 설정 (ROLE_USER 등)
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + user.getRole().name());

        return new UsernamePasswordAuthenticationToken(
                user,
                null,
                Collections.singleton(authority)
        );
    }
}