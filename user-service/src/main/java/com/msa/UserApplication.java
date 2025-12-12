package com.msa; // 🚨 패키지 경로를 프로젝트 표준에 맞게 변경

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient; // 🚨 Eureka 클라이언트 활성화

@SpringBootApplication
@EnableDiscoveryClient // 🚨 유레카 서버에 이 서비스를 등록하겠다고 명시
public class UserApplication {

    public static void main(String[] args) {
        // Spring Boot 애플리케이션 실행
        SpringApplication.run(UserApplication.class, args);
    }
}