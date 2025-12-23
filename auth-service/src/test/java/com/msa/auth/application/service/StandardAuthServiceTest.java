package com.msa.auth.application.service;

import com.msa.auth.adapter.in.web.dto.LoginRequestDto;
import com.msa.auth.application.port.out.LoadUserPort;
import com.msa.auth.application.port.out.TokenProviderPort;
import com.msa.auth.domain.User;
import com.msa.auth.domain.UserRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class) // Mockito 기능을 쓰겠다고 선언
class StandardAuthServiceTest {

    @InjectMocks // 가짜 객체(Mock)들을 주입받을 "진짜" 서비스 객체
    private StandardAuthService standardAuthService;

    @Mock // 가짜 객체 1: DB 조회 담당
    private LoadUserPort loadUserPort;

    @Mock // 가짜 객체 2: 토큰 생성 담당
    private TokenProviderPort tokenProviderPort;

    @Mock // 가짜 객체 3: 비밀번호 암호화 담당
    private PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("로그인 성공: 유효한 아이디와 비밀번호라면 토큰을 반환해야 한다.")
    void login_success() {
        // given (준비)
        String username = "testUser";
        String password = "password123";
        String encodedPassword = "encodedPassword123";
        String expectedToken = "access-token-sample";

        // DTO에 값 설정 (Setter가 없으므로 Reflection 사용)
        LoginRequestDto requestDto = new LoginRequestDto();
        ReflectionTestUtils.setField(requestDto, "username", username);
        ReflectionTestUtils.setField(requestDto, "password", password);

        // 가짜 DB에서 꺼내올 유저 객체 생성
        User mockUser = User.builder()
                .email(username)
                .password(encodedPassword)
                .role(UserRole.USER)
                .build();

        // Mock 객체들의 행동 정의 (시나리오)
        given(loadUserPort.loadUser(username)).willReturn(Optional.of(mockUser));
        given(passwordEncoder.matches(password, encodedPassword)).willReturn(true); // 비번 일치함
        given(tokenProviderPort.createToken(username, "USER")).willReturn(expectedToken);

        // when (실행)
        String token = standardAuthService.login(requestDto);

        // then (검증)
        assertThat(token).isEqualTo(expectedToken); // 결과 토큰이 예상값과 같은지
        verify(loadUserPort).loadUser(username); // 실제로 DB 포트를 호출했는지
        verify(tokenProviderPort).createToken(username, "USER"); // 토큰 생성 포트를 호출했는지
    }

    @Test
    @DisplayName("로그인 실패: 존재하지 않는 아이디라면 예외가 발생해야 한다.")
    void login_fail_user_not_found() {
        // given
        String username = "unknownUser";
        LoginRequestDto requestDto = new LoginRequestDto();
        ReflectionTestUtils.setField(requestDto, "username", username);

        // DB에 유저가 없다고 가정 (Optional.empty)
        given(loadUserPort.loadUser(username)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> standardAuthService.login(requestDto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("사용자를 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("로그인 실패: 비밀번호가 틀리면 예외가 발생해야 한다.")
    void login_fail_wrong_password() {
        // given
        String username = "testUser";
        String password = "wrongPassword";
        String encodedPassword = "encodedPassword123";

        LoginRequestDto requestDto = new LoginRequestDto();
        ReflectionTestUtils.setField(requestDto, "username", username);
        ReflectionTestUtils.setField(requestDto, "password", password);

        User mockUser = User.builder()
                .email(username)
                .password(encodedPassword)
                .role(UserRole.USER)
                .build();

        given(loadUserPort.loadUser(username)).willReturn(Optional.of(mockUser));
        // 비번이 틀렸다고 가정 (false 리턴)
        given(passwordEncoder.matches(password, encodedPassword)).willReturn(false);

        // when & then
        assertThatThrownBy(() -> standardAuthService.login(requestDto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("비밀번호 불일치");
    }
}