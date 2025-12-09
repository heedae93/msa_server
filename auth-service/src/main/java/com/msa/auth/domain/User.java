package com.msa.auth.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "users") // DB 테이블 이름 ('user'는 예약어인 DB가 많아서 users로 설정)
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA용 기본 생성자 (보안상 protected 권장)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING) // DB에 숫자가 아니라 "USER", "ADMIN" 문자열로 저장
    @Column(nullable = false)
    private UserRole role;

    // 객체 생성은 안전하게 Builder 패턴 사용
    @Builder
    public User(String username, String password, UserRole role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }
}