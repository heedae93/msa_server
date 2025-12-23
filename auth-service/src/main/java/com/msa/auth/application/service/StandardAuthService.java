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
public class StandardAuthService implements LoginUseCase, RegisterUseCase {

    private final LoadUserPort loadUserPort;
    private final SaveUserPort saveUserPort;
    private final TokenProviderPort tokenProviderPort;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public String login(LoginRequestDto command) {
        User user = loadUserPort.loadUser(command.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 🚨 변경된 부분: Service가 직접 비교하지 않고 User에게 검증을 "시킵니다".
        user.validatePassword(command.getPassword(), passwordEncoder);

        // 🚨 변경된 부분: 내부 상태를 직접 꺼내기보다 도메인 메서드를 활용합니다.
        return tokenProviderPort.createToken(user.getEmail(), user.getRoleName());
    }

    @Override
    @Transactional
    public void registerUser(SignupRequestDto command) {
        // 1. 중복 확인
        if (loadUserPort.loadUser(command.getEmail()).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }

        // 2. 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(command.getPassword());

        // 3. 빌더를 통한 도메인 객체 생성 (순서 상관 없이 명확하게!)
        User newUser = User.builder()
                .email(command.getEmail())        // 이 순간 validateEmail 실행
                .password(encodedPassword)
                .nickname(command.getNickname())  // 필드 이름 확인 (username -> nickname)
                .role(UserRole.USER)
                .build();                         // 최종적으로 User 생성자 호출

        // 4. 포트를 통해 저장
        saveUserPort.saveUser(newUser);
    }

}

