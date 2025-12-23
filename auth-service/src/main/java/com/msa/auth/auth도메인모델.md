# 📄 도메인 모델 정의서 (Auth Service)

## 1. 개요
본 문서는 인증 서비스(Auth Service)의 핵심 비즈니스 객체인 `User` 도메인과 이를 둘러싼 헥사고날 아키텍처 기반의 비즈니스 규칙을 정의한다.

---

## 2. 핵심 도메인 엔티티: User (사용자)

사용자는 시스템의 인증 및 인가의 주체이며, 다음과 같은 속성과 규칙을 가진다.

### **속성 (Attributes)**
| 속성명 | 타입 | 설명 | 비즈니스 규칙 |
| :--- | :--- | :--- | :--- |
| **id** | Long | 시스템 식별자 | 자동 생성되는 고유 번호 (PK) |
| **email** | String | 로그인 아이디 | 필수, 중복 불가, 로그인 시 식별자로 사용 |
| **password** | String | 인증 암호 | 암호화되어 저장됨 (평문 저장 금지) |
| **nickname** | String | 사용자 별칭 | 시스템 내에서 사용자에게 표시될 이름 |
| **role** | UserRole | 시스템 권한 | 사용자의 접근 범위를 결정 (Enum: USER, ADMIN 등) |

### **핵심 도메인 로직**
- **인증 식별**: `email`은 시스템 전체에서 유일해야 하며, 이를 통해 사용자를 식별한다.
- **권한 관리**: 모든 사용자는 반드시 하나 이상의 `UserRole`을 가져야 하며, 권한에 따라 접근 가능한 기능이 제한된다.

---

## 3. 도메인 서비스 및 유스케이스 (Application Layer)

애플리케이션 계층에서 정의된 유저 관련 핵심 비즈니스 흐름이다.

### **입력 포트 (Inbound Ports / UseCases)**
- **LoginUseCase**: 이메일과 비밀번호를 검증하고, 성공 시 액세스 토큰(JWT)을 발급한다.
- **RegisterUseCase**: 새로운 사용자를 시스템에 등록한다. 이 과정에서 이메일 중복 체크 및 비밀번호 암호화 로직이 수행된다.

### **출력 포트 (Outbound Ports)**
- **LoadUserPort**: 데이터 저장소로부터 특정 조건(Email 등)에 맞는 사용자 정보를 불러온다.
- **SaveUserPort**: 신규 등록되거나 수정된 사용자 정보를 저장소에 영속화한다.
- **TokenProviderPort / JwtTokenValidatorPort**: JWT 토큰의 생성 및 검증을 담당하는 외부 인프라와의 접점이다.

---

## 4. 인프라스트럭처 설계 (Adapter Layer)

도메인 모델을 외부 기술과 연결하는 어댑터들의 구성이다.

### **Persistence Adapter**
- `UserRepository`를 사용하여 `users` 테이블과 매핑된다.
- **Domain Entity ↔ JPA Entity** 간의 변환을 담당하여 도메인 로직의 순수성을 유지한다.

### **Web/Security Adapter**
- `JwtAuthorizationFilter`를 통해 요청의 토큰을 검증한다.
- `AuthController`는 외부의 HTTP 요청을 유스케이스(UseCase)로 전달하는 진입점 역할을 한다.

---

## 5. 비즈니스 예외 처리 (Exception Handling)
- **GlobalExceptionHandler**: 도메인 로직 수행 중 발생하는 예외(중복 계정, 인증 실패 등)를 규격화된 응답으로 변환한다.