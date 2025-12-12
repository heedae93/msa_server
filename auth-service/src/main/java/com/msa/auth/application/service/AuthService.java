package com.msa.auth.application.service;

import com.msa.auth.adapter.in.web.dto.LoginRequestDto;
import com.msa.auth.adapter.in.web.dto.SignupRequestDto;
import com.msa.auth.application.port.in.LoginUseCase;
import com.msa.auth.application.port.in.RegisterUseCase;
import com.msa.auth.application.port.out.LoadUserPort;
import com.msa.auth.application.port.out.SaveUserPort;
import com.msa.auth.application.port.out.TokenProviderPort;
import com.msa.auth.domain.User;
import com.msa.auth.domain.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService implements LoginUseCase, RegisterUseCase { // 인터페이스 추가

    private final LoadUserPort loadUserPort;
    private final SaveUserPort saveUserPort; // 저장용 포트 주입 (New!)
    private final TokenProviderPort tokenProviderPort;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public String login(LoginRequestDto command) {
        User user = loadUserPort.loadUser(command.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        if (!passwordEncoder.matches(command.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("비밀번호 불일치");
        }

        return tokenProviderPort.createToken(user.getEmail(), user.getRole().name());
    }

    // 👇 회원가입 로직 추가
    @Override
    @Transactional
    public void registerUser(SignupRequestDto command) {
        // 1. 중복 확인 (기존 LoadUserPort 활용)
        if (loadUserPort.loadUser(command.getEmail()).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }

        // 2. 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(command.getPassword());

        // 3. 도메인 객체 생성 (User 생성자 필요)
        User newUser = new User(
                command.getEmail(),
                encodedPassword,
                command.getUsername(),
                UserRole.USER
        );

        // 4. 포트를 통해 저장
        saveUserPort.saveUser(newUser);
    }
}