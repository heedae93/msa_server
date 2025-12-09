# WebSecurityConfig  객체 및 메서드 설명
## 전체 코드

```java
package com.msa.auth.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class WebSecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // CSRF 비활성화 (Rest API는 안 씀)
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/login", "/actuator/**").permitAll() // 로그인 경로는 허용
                .anyRequest().authenticated()
            );

        return http.build();
    }
}

```

## 1. 주요 객체 및 메서드 상세 설명

### 1) HttpSecurity (http)
> **비유:** " 비어 있는 건물 보안 시스템 규칙 (Builder)"

* **정체:** 메서드의 파라미터로 주입되는 설정용 객체다.
* **역할:** 보안 설정을 위한 도화지이자 **설정 도구(Builder)** 다.
* **설명:** Spring이 제공하는 빈 설정 객체(`http`)에 점(`.`)을 찍어가며 CSRF 설정, URL별 권한 설정 등의 규칙을 하나씩 추가한다. 
* 마지막에 `.build()`를 호출하여 완성된 보안 필터 체인을 생성한다.
* 보안 필터 체인이란 웹 요청이 서버(컨트롤러)에 도달하기 전에 반드시 거쳐야 하는
여러 개의 보안 관문을 순서대로 연결해 놓은 것

### 2) SecurityFilterChain
> **비유:** "완성된 보안 요원 팀 (Result)"

* **정체:** 메서드의 **반환 타입(Return Type)** 이자 Bean으로 등록되는 객체다.
* **역할:** 실제로 애플리케이션에 적용되어 요청을 가로채고 검사하는 보안 필터들의 묶음이다.
* **설명:** `http.build()`의 결과물이다. Spring Boot는 이 Bean을 감지하여 애플리케이션의 보안 로직(문지기 역할)으로 적용한다.

### 3) csrf(csrf -> csrf.disable())
> **비유:** "위조 방지 시스템 스위치 끄기"

* **정체:** CSRF(Cross Site Request Forgery, 사이트 간 요청 위조) 방어 기능 설정이다.
* **설명:**
    * **기본값:** Spring Security는 기본적으로 CSRF 방어가 켜져 있다.
    * **왜 끄는가?:** CSRF는 주로 세션/쿠키 기반 인증에서 발생하는 취약점이다. 현재 REST API + JWT 토큰 방식을 사용하므로(Stateless), 세션을 사용하지 않아 CSRF 공격으로부터 구조적으로 안전하다. 따라서 원활한 API 호출을 위해 비활성화(`disable`)한다.
* **CSRF(사이트 간 요청 위조)** 란?

    * 정의: 사용자가 자신의 의지와는 무관하게 공격자가 의도한 행위(수정, 삭제, 등록, 결제 등)를 특정 웹사이트에 요청하게 만드는 공격이다.

    * 상황 예시:
      * 사용자가 은행 사이트(bank.com)에 로그인해서 **인증된 상태(쿠키 보유)**다.
      * 로그아웃을 안 한 상태로, 공격자가 만든 낚시 사이트(evil.com)에 접속했다.
      * 낚시 사이트에 들어가는 순간, 숨겨진 스크립트가 실행되어 **"내 계좌에서 공격자 계좌로 100만 원 이체해"**라는 요청을 bank.com으로 날린다.
      * bank.com 서버는 이 요청이 진짜 사용자가 보낸 건지, 낚시 사이트가 보낸 건지 구분 못하고 돈을 이체해버린다.
### 4) authorizeHttpRequests(auth -> ...)
> **비유:** "출입 명부 작성 (인가, Authorization)"

* **정체:** HTTP 요청에 대한 **접근 권한(인가)** 을 설정하는 곳이다.
* **역할:** "누가 어디로 갈 수 있는지" URL별 규칙을 정의한다.

### 5) requestMatchers(...)
> **비유:** "특정 구역 지목"

* **정체:** 특정 URL 패턴을 선택하는 메서드다. (구 `antMatchers`)
* **예시:**
    * `"/login"`: 로그인 요청 URL
    * `"/actuator/**"`: 유레카(Eureka)나 모니터링 도구의 헬스 체크 URL

### 6) permitAll() vs authenticated()
> **비유:** "프리패스" vs "신분증 검사"

* **`permitAll()`**: "무조건 허용". 로그인하지 않은 사용자도 접근 가능하다. (로그인 페이지, 회원가입 등)

* **`anyRequest()`**: 위에서 설정한 URL들을 제외한 나머지 모든 요청을 의미한다.
* **`authenticated()`**: "인증된 사용자만 허용". 로그인 성공 후 토큰을 가진 사용자만 접근 가능하다.
* 정리하면 permitAll()에 들어가 있는 경로에서 은 요청은 "누구나 들어올 수 있어"이고, 나머지 경로에서 온 요청은 "신분증(인증 토큰) 있는 사람만 통과 시켜 줄게"라는 뜻이다.
---

## 2. 코드의 흐름 요약

이 코드를 "경비원에게 내리는 업무 지시"로 번역하면 다음과 같다.

> "Spring아(`@Bean`), 보안 시스템(`SecurityFilterChain`) 하나 만들어줘.
> 내가 설정판(`http`) 줄 테니까 이렇게 세팅해:
>
> 1. CSRF 방어 기능은 꺼. (우린 REST API 쓸 거니까)
> 2. 출입 규칙(`authorizeHttpRequests`)은 다음과 같아:
     >    * `/login` 이랑 `/actuator` 쪽으로 가는 사람은 그냥 들여보내(`permitAll`).
>    * 그 외에 **나머지 모든 곳(`anyRequest`)**으로 가려는 사람은 신분증 확인(`authenticated`) 꼭 해!
>
> 다 됐으면 빌드(`build`)해서 완성품 내놔."