package com.msa.auth.adapter.in.web;

import com.msa.auth.adapter.in.web.dto.LoginRequestDto;
import com.msa.auth.application.port.in.LoginUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class AuthController {

    // 서비스의 구체적인 이름은 몰라도 됨. "로그인 기능(UseCase)"만 있으면 됨.
    private final LoginUseCase loginUseCase;

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody LoginRequestDto request) {
        String token = loginUseCase.login(request);
        return ResponseEntity.ok(Map.of("token", token));
    }
}