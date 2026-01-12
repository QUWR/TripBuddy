# TripBuddy Code Convention

이 문서는 TripBuddy 프로젝트의 코드 스타일과 아키텍처 규칙을 정의합니다. 
CodeRabbit 및 팀원은 코드 리뷰 시 이 규칙을 준수해야 합니다.

## 1. Architecture & Package Structure
- **Layered Architecture:** `Controller` -> `Service` -> `Repository`의 흐름을 엄격히 준수한다.
- **Domain-Driven Packaging:** 패키지는 `domain/{domain_name}/{layer}` 형식을 따른다. (예: `domain.user.controller`, `domain.plan.service`)
- **Global Config:** 공통 설정 및 유틸리티는 `global` 패키지 하위에 위치한다.

## 2. Entity & Database
- **Lombok Usage:**
  - `@Getter`, `@Builder`, `@NoArgsConstructor(access = AccessLevel.PROTECTED)`, `@AllArgsConstructor`를 기본으로 사용한다.
  - **금지:** `@Setter`와 `@Data`는 데이터 무결성을 위해 사용을 지양한다.
- **Inheritance:** 모든 엔티티는 `BaseEntity`를 상속받아 `createdAt`, `updatedAt`을 관리한다.
- **Relationships:**
  - 모든 `Example: @ManyToOne`, `@OneToOne` 관계는 반드시 `fetch = FetchType.LAZY`로 설정한다.
  - 컬렉션 필드(`@OneToMany`)는 필드 선언 시 `new ArrayList<>()`로 초기화하고 `@Builder.Default`를 붙인다.
- **Concurrency:** 동시성 이슈가 예상되는 엔티티(예: `Schedule`)에는 `@Version`을 사용하여 낙관적 락(Optimistic Lock)을 적용한다.
- **Data Types:** 금액과 관련된 필드(예: `Budget`)는 오차 방지를 위해 `BigDecimal`을 사용한다.

## 3. DTO (Data Transfer Object)
- **Separation:** Controller는 절대로 Entity를 직접 반환하거나 파라미터로 받지 않는다. 반드시 `Request/Response` DTO를 사용한다.
- **Validation:** 입력 데이터 검증은 DTO 내부에서 `jakarta.validation` 어노테이션(`@NotBlank`, `@Email` 등)을 사용하여 처리한다.
- **Mapping:** Entity <-> DTO 변환은 DTO 내부에 `from()`, `toEntity()`와 같은 정적 팩토리 메서드나 인스턴스 메서드로 구현한다.

## 4. Controller & Service
- **Response:** 모든 API 응답은 `ResponseEntity`로 감싸서 반환한다.
- **Dependency Injection:** `@Autowired` 대신 `@RequiredArgsConstructor`를 통한 생성자 주입을 사용한다.
- **Transactional:** - Service 클래스 레벨에 `@Transactional(readOnly = true)`를 적용한다.
  - 데이터 변경(CUD)이 일어나는 메서드에만 `@Transactional`을 별도로 붙인다.

## 5. Exception Handling
- **Custom Exception:** 비즈니스 로직 예외는 `RuntimeException` 대신 `CustomException`을 상속받아 구현한다.
- **ErrorCode:** 예외 메시지와 상태 코드는 `ErrorCode` Enum에서 중앙 관리한다.
- **Global Handling:** 모든 예외는 `GlobalExceptionHandler`(`@RestControllerAdvice`)에서 처리하여 일관된 JSON 포맷(`ErrorResponse`)으로 반환한다.

## 6. Coding Style
- **Naming:**
  - Class: `PascalCase`
  - Method/Variable: `camelCase`
  - DB Table/Column: `snake_case` (JPA Naming Strategy에 따름)
- **Formatting:** 메서드 체이닝 시 가독성을 위해 줄바꿈을 적극 활용한다 (특히 QueryDSL이나 Builder 패턴 사용 시).

## 7. Testing
- **Framework:** JUnit 5와 Mockito를 기본으로 사용한다.
- **Scope:**
  - Controller: `@WebMvcTest`
  - Service: Mockito를 활용한 단위 테스트
  - Repository: `@DataJpaTest`