package com.msa.auth.adapter.in.web;

import com.msa.auth.adapter.in.web.dto.LoginRequestDto;
import com.msa.auth.adapter.in.web.dto.SignupRequestDto;
import com.msa.auth.application.port.in.LoginUseCase;
import com.msa.auth.application.port.in.RegisterUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class StandardAuthController {

    // 서비스의 구체적인 구현체는 몰라도 됨. "UseCase"만 알면 된다.
    private final RegisterUseCase registerUseCase;
    private final LoginUseCase loginUseCase;


    @PostMapping("/auth/signup")
    public ResponseEntity<String> signup(@RequestBody SignupRequestDto request) {
        registerUseCase.registerUser(request);
        return ResponseEntity.ok("회원가입 성공!");
    }

    @PostMapping("/auth/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody LoginRequestDto request) {
        String token = loginUseCase.login(request);
        return ResponseEntity.ok(Map.of("token", token));
    }

}