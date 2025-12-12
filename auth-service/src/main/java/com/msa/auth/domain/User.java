package com.msa.auth.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 🚨 [수정] username 대신 email 필드를 사용합니다. (로그인 ID 역할)
    @Column(nullable = false, unique = true, length = 50)
    private String email;

    @Column(nullable = false)
    private String password;

    // 🚨 [추가] 닉네임 필드는 그대로 유지합니다.
    @Column(nullable = false, length = 50)
    private String nickname;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    @Builder
    // 🚨 [수정] 생성자 순서와 이름 변경 (email, password, nickname, role 순)
    public User(String email, String password, String nickname, UserRole role) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.role = role;
    }

    // (이전 코드에서 사용하던 getter/setter는 Lombok이 처리)
}