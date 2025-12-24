# TripBuddy
## 📝 프로젝트 소개

TripBuddy는 단순한 여행 정보 공유를 넘어, 사용자 간의 상호작용과 데이터의 효율적 관리에 초점을 맞춘 프로젝트입니다.

여행 정보를 공유하는 블로그 기능과 WebSocket을 활용한 실시간 협업 여행 플래너를 통해 여행의 준비부터 기록까지의 모든 과정을 지원합니다.

- **개발 기간:** 2024.XX ~ 2024.XX (진행 중)
- **개발 인원:** 1명 (백엔드)

---

## ✨ 주요 기능

### 1. 🔐 인증 및 사용자 관리 (Auth)

- **JWT 기반 인증:** Access Token과 Refresh Token을 활용한 보안 강화 및 Stateless 인증 구현.
- **커스텀 필터:** `LoginFilter`와 `JwtFilter`를 통해 인증 프로세스 세분화 및 예외 처리.
- **회원가입:** 비밀번호 암호화(BCrypt) 및 이메일/닉네임 중복 검증.

### 2. 📝 콘텐츠 관리 (Contents)

- **게시글 CRUD:** 여행 팁(TIP)과 리뷰(REVIEW) 카테고리별 게시글 작성 및 조회.
- **마크다운 이미지 처리:** 본문 내 이미지 URL 추출 및 관리 로직 구현.
- **이미지 생명주기 관리:** - 게시글 작성 시 이미지는 `TEMP` 상태로 업로드.
    - 게시글 저장 시 실제 사용된 이미지만 `ACTIVE` 상태로 전환.
    - **스케줄러:** 매일 새벽 3시에 고아 객체(사용되지 않는 `TEMP` 이미지)를 S3와 DB에서 자동 삭제하여 스토리지 비용 절감 (`ImageCleanupScheduler`).

### 3. 💬 소통 및 반응 (Interaction)

- **댓글 시스템:** 게시글에 대한 댓글 작성, 수정, 삭제 기능.
- **좋아요(Like):** 게시글 및 댓글에 대한 좋아요 토글 기능 및 카운트 동기화.

### 4. 🗺️ 실시간 여행 플래너 (Planner)

- **여행 계획 생성:** 제목, 날짜, 초대 코드를 포함한 플랜 생성.
- **멤버 초대:** 고유 초대 코드를 통한 멤버 입장 기능.
- **실시간 동기화:** **WebSocket & STOMP**를 활용하여 여러 사용자가 여행 일정(`Schedule`)을 동시에 편집하고 실시간으로 반영.
- **예산 관리(Budget):** 여행 경비 및 카테고리별 지출 관리.

---

## 🛠 기술 스택

| **분류** | **기술** | **상세 내용** |
| --- | --- | --- |
| **Language** | Java 21 | 최신 LTS 버전 사용 |
| **Framework** | Spring Boot 3.5.3 | 웹 애플리케이션 프레임워크 |
| **Build Tool** | Gradle | 의존성 관리 및 빌드 자동화 |
| **Database** | MySQL, H2 | 프로덕션(MySQL), 테스트(H2) 환경 분리 |
| **ORM** | Spring Data JPA | 객체 지향적인 데이터 접근 계층 구현 |
| **Security** | Spring Security, JWT | 인증 및 권한 관리 |
| **Storage** | AWS S3 | 이미지 파일 저장소 |
| **Real-time** | WebSocket (STOMP) | 실시간 양방향 통신 |
| **Docs** | Swagger (SpringDoc) | API 명세서 자동화 |

---

## 🏗 시스템 아키텍처 및 핵심 기술

### 🖼️ 이미지 업로드 프로세스 최적화

사용자가 글을 쓰다가 이탈하거나 이미지를 삭제했을 때 발생하는 **불필요한 이미지(Garbage Image)** 문제를 해결하기 위해 상태 기반 관리 시스템을 도입했습니다.

1. 이미지 업로드 시 `TEMP` 상태로 저장.
2. 게시글 최종 저장 시 본문을 파싱하여 실제 포함된 이미지만 `ACTIVE`로 변경.
3. Spring Scheduler가 주기적으로 `TEMP` 상태의 오래된 이미지를 S3에서 삭제.

### 🧩 확장성 있는 도메인 설계

- **Enum 활용:** `ContentType`(TIP, REVIEW), `RoleType`(USER, ADMIN), `ImageStatus` 등을 Enum으로 관리하여 Type Safety 보장.
- **Converter:** `StringToContentTypeConverter`를 통해 URL 파라미터를 Enum으로 자동 변환하여 컨트롤러 코드 간소화.

---

## 🚀 실행 방법

### 1. 사전 요구사항 (Prerequisites)

- Java 21 이상
- MySQL 8.0 이상
- AWS 계정 (S3 버킷 생성 및 Access Key 발급 필요)

### 2. 환경 변수 설정 (.env)

프로젝트 루트 경로에 `.env` 파일을 생성하거나 환경 변수를 설정해야 합니다.

Properties

```jsx
# Server
SERVER_PORT=8080

# Database
SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/tripbuddy
SPRING_DATASOURCE_USERNAME=your_username
SPRING_DATASOURCE_PASSWORD=your_password

# JWT
JWT_SECRET_KEY=your_base64_encoded_secret_key
JWT_ACCESS_EXP_TIME=3600000
JWT_REFRESH_EXP_TIME=86400000

# AWS S3
CLOUD_AWS_CREDENTIALS_ACCESS_KEY=your_aws_access_key
CLOUD_AWS_CREDENTIALS_SECRET_KEY=your_aws_secret_key
CLOUD_AWS_REGION_STATIC=ap-northeast-2
CLOUD_AWS_S3_BUCKET=your_s3_bucket_name

# Gemini API (Optional)
GEMINI_API_KEY=your_gemini_api_key
```

### 3. 빌드 및 실행

Bash

```bash
# Clone Repository
git clone https://github.com/your-username/tripbuddy.git

# Build
./gradlew build

# Run
./gradlew bootRun
```

---

## 📘 API 문서

서버 실행 후 아래 주소에서 Swagger UI를 통해 API 문서를 확인할 수 있습니다.

- **URL:** `http://localhost:8080/swagger-ui/index.html`

---

## 🤝 Contributing

이 프로젝트는 개인 학습 및 포트폴리오 목적으로 개발되었습니다. 버그 리포트나 기능 제안은 Issue를 통해 환영합니다.

---

**Contact:** 박연준 (hnn06134@gmail.com)
