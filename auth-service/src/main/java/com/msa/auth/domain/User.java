package com.msa.auth.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.regex.Pattern;

@Entity
@Getter
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

    // 간단한 이메일 정규식 패턴
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, length = 50)
    private String nickname;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    @Builder
    public User(String email, String password, String nickname, UserRole role) {
        // 🚨 생성 시점에 도메인 규칙 검증
        validateEmail(email);
        validateNickname(nickname);

        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.role = role != null ? role : UserRole.USER;
    }

    /**
     * 행위: 이메일 형식 유효성 검증
     * 생성 시점에 호출되어, 도메인 규칙(정규식)에 어긋나는 유저 객체 생성을 차단합니다.
     */
    private void validateEmail(String email) {
        if (email == null || !EMAIL_PATTERN.matcher(email).matches()) {
            throw new IllegalArgumentException("올바르지 않은 이메일 형식입니다.");
        }
    }

    /**
     * 행위: 닉네임 필수값 검증
     * 유저의 식별 가능한 별칭이 비어있지 않은지 도메인 내부에서 스스로 확인합니다.
     */
    private void validateNickname(String nickname) {
        if (nickname == null || nickname.trim().isEmpty()) {
            throw new IllegalArgumentException("닉네임은 필수입니다.");
        }
    }

    /**
     * 행위: 비밀번호 일치 여부 확인
     * 외부에서 주입된 PasswordEncoder를 사용하여, 객체 내부의 암호화된 비밀번호와 비교를 수행합니다.
     * (데이터를 밖으로 꺼내지 않고 객체에게 검증을 요청하는 'Tell, Don't Ask' 원칙 준수)
     */
    public void validatePassword(String rawPassword, PasswordEncoder passwordEncoder) {
        if (!passwordEncoder.matches(rawPassword, this.password)) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
    }

    /**
     * 행위: 사용자 권한(Role) 명칭 반환
     * 도메인의 상태(Enum)를 문자열로 변환하여 제공함으로써 내부 구현을 캡슐화합니다.
     */
    public String getRoleName() {
        return this.role.name();
    }
}