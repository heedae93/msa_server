package com.msa.auth.adapter.out.jwt;

import com.msa.auth.application.port.out.TokenProviderPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtAdapter implements TokenProviderPort {

    private final JwtUtil jwtUtil; // 기존에 만든 그 유틸

    @Override
    public String createToken(String userId, String role) {
        return jwtUtil.createToken(userId, role);
    }
}
