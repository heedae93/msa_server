package com.msa.auth.adapter.in.web.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor // ✅ 추가 (필터 주입을 위해)
public class WebSecurityConfig {

    // 👇 우리가 만든 필터 주입
    private final JwtAuthorizationFilter jwtAuthorizationFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring()
                // 1. GET 요청 그룹
                .requestMatchers(
                        HttpMethod.GET,
                        "/view/**",
                        "/favicon.ico",
                        "/css/**", "/js/**", "/images/**"
                )
                // 2. POST 요청 그룹을 별도의 requestMatchers 호출로 분리
                .requestMatchers(
                        HttpMethod.POST,
                        "/auth/signup",
                        "/auth/login"
                );
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .headers(headers -> headers.frameOptions(frameOptions -> frameOptions.disable()))
                // ✅ 세션 사용 안 함 (JWT를 쓰니까 상태 없음 모드로 설정)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login", "/actuator/**").permitAll()
                        .requestMatchers("/h2-console/**").permitAll()
                        .anyRequest().authenticated()
                )

                // ✅ 필터 배치: UsernamePasswordAuthenticationFilter(기본 로그인) 앞에 배치
                .addFilterBefore(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}