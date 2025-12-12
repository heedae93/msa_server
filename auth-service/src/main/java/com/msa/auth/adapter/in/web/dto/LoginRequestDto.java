package com.msa.auth.adapter.in.web.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor // 기본 생성자 (JSON -> 객체 변환 시 필수)
public class LoginRequestDto {

    private String email;
    private String password;


}
