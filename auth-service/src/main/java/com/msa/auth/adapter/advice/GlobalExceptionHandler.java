package com.msa.auth.adapter.advice; // 예시 패키지 경로

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 1. 비즈니스 규칙 위반 (예: 이미 존재하는 이메일)
    // Use Case (application/service)에서 던지는 예외를 여기서 잡습니다.
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException e) {
        // HTTP 400 Bad Request로 응답
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }

    // 2. 기타 모든 예상치 못한 서버 예외 (최후의 방어선)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleAllExceptions(Exception e) {
        // 서버 로그에 예외를 기록하고, 클라이언트에게는 일반적인 500 에러를 반환
        // e.printStackTrace(); // 로깅 시스템 사용 권장
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("시스템 처리 중 예상치 못한 오류가 발생했습니다.");
    }
}