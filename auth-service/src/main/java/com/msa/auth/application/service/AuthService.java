package com.msa.auth.application.service;

import com.msa.auth.adapter.in.web.dto.LoginRequestDto;
import com.msa.auth.application.port.in.LoginUseCase;
import com.msa.auth.application.port.out.LoadUserPort;
import com.msa.auth.application.port.out.TokenProviderPort;
import com.msa.auth.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService implements LoginUseCase {

    // 구체적인 기술(Repository) 대신 추상적인 인터페이스 사용
    private final LoadUserPort loadUserPort;
    private final TokenProviderPort tokenProviderPort;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public String login(LoginRequestDto command) {
        // 1. 포트를 통해 사용자 조회
        User user = loadUserPort.loadUser(command.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 2. 비번 검증
        if (!passwordEncoder.matches(command.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("비밀번호 불일치");
        }

        // 3. 포트를 통해 토큰 생성
        return tokenProviderPort.createToken(user.getUsername(), user.getRole().name());
    }
}