package com.msa.gateway.filter; // 게이트웨이 모듈에 맞게 경로 설정

import com.msa.gateway.util.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
// GlobalFilter: 모든 라우트에 적용되는 필터
// Ordered: 필터의 실행 순서를 지정
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {

    private final JwtUtil jwtUtil;

    // 🚨 인증 없이 통과시킬 경로 목록 (로그인, 회원가입, 뷰 페이지 등)
    private static final List<String> EXCLUDED_PATHS = List.of(
            "/auth/login",
            "/auth/signup",
            "/view"
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();

        // 1. 제외 목록 확인 (로그인/회원가입 등은 바로 통과)
        if (isExcluded(path)) {
            return chain.filter(exchange);
        }

        // 2. Authorization 헤더 확인 및 토큰 추출
        String token = resolveToken(request.getHeaders());

        // 3. 토큰이 없는 경우 -> 401 에러 반환
        if (token == null) {
            log.warn("토큰이 없는 요청: {}", path);
            return onError(exchange, "JWT Token is missing.", HttpStatus.UNAUTHORIZED);
        }

        // 4. 토큰 유효성 검사 및 사용자 정보 추출
        try {
            jwtUtil.validateToken(token); // 🚨 토큰 유효성 검증
            Claims claims = jwtUtil.getUserInfoFromToken(token);
            String userId = claims.getSubject(); // Subject = 이메일

            // 5. 유효하다면, 사용자 ID를 헤더에 주입하여 다음 서비스로 전달
            ServerHttpRequest authorizedRequest = request.mutate()
                    // 🚨 [핵심] 다음 마이크로서비스(ai-service)가 사용할 사용자 정보
                    .header("X-User-ID", userId)
                    .build();

            return chain.filter(exchange.mutate().request(authorizedRequest).build());

        } catch (Exception e) {
            log.error("JWT 토큰 검증 실패: {}", e.getMessage());
            // 401 에러 반환 (만료, 변조 등)
            return onError(exchange, "Invalid or Expired JWT Token.", HttpStatus.UNAUTHORIZED);
        }
    }

    // 필터 실행 순서 (가장 먼저 실행되도록 설정)
    @Override
    public int getOrder() {
        return -1; // -1이 가장 높은 우선순위
    }

    // --- 헬퍼 메서드 ---

    private boolean isExcluded(String path) {
        // 요청 경로가 /auth/login 또는 /view/ 로 시작하는지 확인
        return EXCLUDED_PATHS.stream().anyMatch(path::startsWith) || path.startsWith("/view");
    }

    private String resolveToken(HttpHeaders headers) {
        String bearerToken = headers.getFirst(HttpHeaders.AUTHORIZATION);
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus httpStatus) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);
        return response.setComplete(); // 응답 본문 없이 상태 코드만 반환
    }
}