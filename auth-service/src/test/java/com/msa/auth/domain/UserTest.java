package com.msa.auth.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Nested
    @DisplayName("User 생성 테스트")
    class CreateUser {

        @Test
        @DisplayName("올바른 정보로 유저 생성 시 성공한다")
        void success() {
            // given & when
            User user = User.builder()
                    .email("test@example.com")
                    .password("encoded_password")
                    .nickname("테스터")
                    .role(UserRole.USER)
                    .build();

            // then
            assertThat(user.getEmail()).isEqualTo("test@example.com");
            assertThat(user.getNickname()).isEqualTo("테스터");
            assertThat(user.getRoleName()).isEqualTo("USER");
        }

        @Test
        @DisplayName("잘못된 이메일 형식으로 생성 시 IllegalArgumentException이 발생한다")
        void failInvalidEmail() {
            assertThatThrownBy(() -> User.builder()
                    .email("invalid-email")
                    .password("pw")
                    .nickname("nick")
                    .build())
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("올바르지 않은 이메일 형식입니다.");
        }

        @Test
        @DisplayName("닉네임이 공백일 경우 생성에 실패한다")
        void failEmptyNickname() {
            assertThatThrownBy(() -> User.builder()
                    .email("test@test.com")
                    .password("pw")
                    .nickname("  ")
                    .build())
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("닉네임은 필수입니다.");
        }
    }

    @Nested
    @DisplayName("비밀번호 검증 테스트")
    class ValidatePassword {

        @Test
        @DisplayName("일치하는 비밀번호를 입력하면 예외가 발생하지 않는다")
        void success() {
            // given
            String rawPassword = "password123!";
            String encodedPassword = passwordEncoder.encode(rawPassword);
            User user = User.builder()
                    .email("test@test.com")
                    .password(encodedPassword)
                    .nickname("nick")
                    .build();

            // when & then
            user.validatePassword(rawPassword, passwordEncoder);
        }

        @Test
        @DisplayName("일치하지 않는 비밀번호를 입력하면 IllegalArgumentException이 발생한다")
        void fail() {
            // given
            String rawPassword = "password123!";
            String encodedPassword = passwordEncoder.encode(rawPassword);
            User user = User.builder()
                    .email("test@test.com")
                    .password(encodedPassword)
                    .nickname("nick")
                    .build();

            // when & then
            assertThatThrownBy(() -> user.validatePassword("wrong_password", passwordEncoder))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("비밀번호가 일치하지 않습니다.");
        }
    }
}