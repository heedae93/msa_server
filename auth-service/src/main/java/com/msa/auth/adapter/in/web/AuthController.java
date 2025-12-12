package com.msa.auth.adapter.in.web;

import com.msa.auth.adapter.in.web.dto.LoginRequestDto;
import com.msa.auth.adapter.in.web.dto.SignupRequestDto;
import com.msa.auth.application.port.in.LoginUseCase;
import com.msa.auth.application.port.in.RegisterUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class AuthController {

    // 서비스의 구체적인 이름은 몰라도 됨. "UseCase"만 있으면 됨.
    private final RegisterUseCase registerUseCase;
    private final LoginUseCase loginUseCase;

    // 👇 4. [추가됨] 회원가입 API (실제 주소: POST /auth/signup)
    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody SignupRequestDto request) {
        registerUseCase.registerUser(request);
        return ResponseEntity.ok("회원가입 성공!");
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody LoginRequestDto request) {
        String token = loginUseCase.login(request);
        return ResponseEntity.ok(Map.of("token", token));
    }

    // 👇 [테스트용] 로그인한 사람만 접근 가능한 API
    @GetMapping("/test")
    public ResponseEntity<String> test() {
        // 필터를 통과해서 저장된 인증 객체 꺼내기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        return ResponseEntity.ok("인증 성공! 당신의 ID는: " + authentication.getName());
    }
}