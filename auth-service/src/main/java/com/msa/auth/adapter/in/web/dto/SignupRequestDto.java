package com.msa.auth.adapter.in.web.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter // 모든 필드의 Getter 자동 생성
@Setter // 모든 필드의 Setter 자동 생성
@NoArgsConstructor // 기본 생성자 자동 생성 (필수!)
public class SignupRequestDto {
    private String email;
    private String password;
    private String username;

}
