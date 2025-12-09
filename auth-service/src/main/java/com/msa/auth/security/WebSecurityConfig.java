package com.msa.auth.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class WebSecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // CSRF 비활성화 (Rest API는 안 씀)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login", "/actuator/**").permitAll() // 로그인 경로는 허용
                        .anyRequest().authenticated()
                );

        return http.build();
    }
}
