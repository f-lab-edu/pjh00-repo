# AGENTS.md (Korean)

> **기준 문서 (Canonical은 영문)**  
> 본 문서는 한글 참고용이다.  
> 해석에 모호함이 있을 경우 영문 Canonical 버전을 우선한다.

---

## 1. 프로젝트 구조 & 모듈 구성

이 저장소는 Gradle 멀티모듈 워크스페이스이며, 마이크로서비스 아키텍처(MSA)를 따른다.

- 공용 계약(contracts)과 유틸리티는 `common/`에 위치한다.
- 각 API 서비스(예: `order-api`, `product-api`)는 독립적인 Spring Boot 애플리케이션이다.
- 인프라 매니페스트는 `docker-compose/` 하위에 둔다.

각 모듈은 표준 Spring Boot 구조를 따른다.

- `src/main/java` – 애플리케이션 코드
- `src/main/resources` – 설정(YAML)
- `src/test/java` – 테스트

**규칙**
- 서비스 모듈은 다른 서비스 모듈에 의존하면 안 된다.
- 허용되는 의존성:
    - `<service>-api` → `common`
    - `common` → (Spring / Spring Boot 의존 금지)
- 각 서비스는 자신의 DB 스키마와 Flyway 마이그레이션을 소유한다.

---

## 2. 아키텍처 규칙 (Hexagonal Architecture)

모든 API 모듈은 Hexagonal Architecture를 반드시 준수한다.

**레이어 규칙**
- Controller, Kafka Listener는 반드시 `adapters.in` 하위에 위치한다.
- Persistence, Kafka Producer, Redis, 외부 API Client는 반드시 `adapters.out` 하위에 위치한다.
- 비즈니스 로직은 반드시 `application` 또는 `domain`에 위치한다.
- Application 레이어는 Port(인터페이스)에만 의존해야 하며 Adapter 구현체에 직접 의존하면 안 된다.
- Domain 모델은 Spring/JPA/인프라에 의존하면 안 된다.

**권장 패키지 구조**
~~~text
com.pjh.<service>
 ├─ adapters
 │   ├─ in
 │   │   ├─ web
 │   │   └─ kafka
 │   └─ out
 │       ├─ persistence
 │       ├─ kafka
 │       └─ redis
 ├─ application
 │   ├─ port
 │   │   ├─ in
 │   │   └─ out
 │   └─ service
 ├─ domain
 │   ├─ model
 │   └─ policy
 └─ infrastructure
~~~

---

## 3. common 모듈 규칙

`common` 모듈은 **순수 Java 라이브러리**여야 한다.

**common에 포함 금지**
- Spring annotation
- Spring Boot dependency
- JPA entity 또는 repository
- `@Configuration`, `@Component` 등 프레임워크 어노테이션

**common에 포함 가능**
- DTO / API Contract
- Error code 및 공통 Error Response 모델
- Domain Event 및 Event Schema
- Utility / constant

---

## 4. 데이터베이스 & 데이터 소유권 규칙

- 각 서비스는 독립 DB(스키마 또는 물리 인스턴스)를 소유한다.
- 타 서비스 DB 접근은 절대 금지한다.
- Cross-DB join 및 FK 사용은 금지한다.
- 타 서비스의 ID는 논리적으로만 참조하며 FK로 연결하지 않는다.

**Soft Delete**
- 모든 삭제는 Soft Delete(`is_deleted`, `deleted_at`)를 사용한다.
- 활성 데이터 조회는 `is_deleted = 0` 조건을 포함해야 한다.
- UNIQUE 제약은 필요 시 `is_deleted`를 포함해야 한다.

---

## 5. 트랜잭션 & Outbox 규칙

- 트랜잭션 경계는 application service(use-case) 레벨에서 정의한다.
- 상태 변경과 Outbox 레코드는 동일 트랜잭션에서 기록해야 한다.
- Kafka 발행은 비동기이며 별도 퍼블리셔에서 처리한다.
- Outbox 레코드는 생성 이후 불변(immutable)이어야 한다.

---

## 6. 메시징 & Kafka 규칙

**Event 규칙**
- 이벤트는 이미 발생한 사실을 표현한다.
- 이벤트는 불변이어야 한다.
- 이벤트는 재처리 가능해야 한다.

**Kafka Envelope 필수 필드**
- `eventId`
- `eventType`
- `occurredAt`
- `traceId`
- `producer`
- `schemaVersion`
- Deterministic message key

**Consumer 규칙**
- Consumer는 반드시 멱등(idempotent)해야 한다.
- 중복 메시지를 안전하게 처리해야 한다.
- Retry topic 및 DLQ를 사용해야 한다.
- 메시지 처리는 핵심 비즈니스 플로우를 블로킹하면 안 된다.

---

## 7. API & 에러 처리 규칙

- API 응답은 통일된 response envelope을 따른다.
- Domain exception은 공통 error code로 매핑되어야 한다.
- `@ControllerAdvice`는 `adapters.in.web`에만 존재해야 한다.
- 요청 검증은 Adapter 레벨에서 수행한다.
- Domain invariant는 domain/application 레벨에서 강제한다.

---

## 8. 테스트 규칙

**테스트 전략**
- JUnit5 필수
- 테스트 슬라이스 우선:
    - `@DataJpaTest`
    - `@WebMvcTest`
- `@SpringBootTest`는 통합 플로우 검증에만 제한적으로 사용한다.

**Testcontainers**
- MariaDB, Kafka, Redis는 Testcontainers로 실행해야 한다.
- 테스트는 외부 서비스에 의존하면 안 된다.

**Contract**
- API contract는 OpenAPI 또는 REST Docs 사용을 권장한다.
- Kafka event schema는 contract test를 권장한다.

---

## 9. 성능 & 부하 테스트

- k6를 사용한다.
- 성능 목표는 정의된 SLO와 일치해야 한다.
- 부하 테스트 스크립트는 `/performance/k6` 하위에 둔다.

---

## 10. 보안 & 설정 규칙

- Secret은 레포에 커밋 금지
- 설정은 환경 변수로 외부화
- 민감 값은 `@ConfigurationProperties`로 주입
- 로그는 구조화(JSON)
- PII는 로그에 남기지 않는다.

---

## 11. 금지 사항

- JPA Entity를 API DTO로 사용 금지
- Entity를 서비스 경계 밖으로 노출 금지
- `RestTemplate` 사용 금지 (`WebClient` 권장)
- application 레이어를 우회한 persistence 접근 금지
- 순환 의존성(circular dependency) 금지

---

[//]: # (## 12. Catalog &#40;고객 조회&#41; Redis 규칙 – product-api)

[//]: # ()
[//]: # (product-api의 Catalog는 **고객 조회&#40;Read&#41; 전용**이며 Redis 기반으로 제공한다.)

[//]: # ()
[//]: # (### 12.1 기본 원칙)

[//]: # (- Catalog 조회 API는 Redis를 우선 사용한다.)

[//]: # (- DB fallback 허용 여부는 명확히 정의한다.)

[//]: # (    - &#40;권장/MVP&#41; Redis miss 시 DB fallback 허용 + read-through 방식으로 Redis 채우기)

[//]: # (- Catalog API는 JPA Entity를 노출하지 않으며 조회 전용 DTO/View로 응답한다.)

[//]: # ()
[//]: # (### 12.2 Redis 키 규칙&#40;권장&#41;)

[//]: # (- 상품 상세:)

[//]: # (    - `catalog:product:{productId}` &#40;Hash&#41;)

[//]: # (- 검색 인덱스:)

[//]: # (    - 키워드 토큰 인덱스: `catalog:kw:{token}` &#40;Set of productId&#41;)

[//]: # (    - 카테고리 필터: `catalog:category:{categoryId}` &#40;Set of productId&#41;)

[//]: # (    - 가격 범위: `catalog:price` &#40;ZSet, score=price, member=productId&#41;)

[//]: # (    - 최신순: `catalog:newest` &#40;ZSet, score=createdAtEpoch, member=productId&#41;)

[//]: # (    - &#40;옵션&#41; 인기순: `catalog:popularity` &#40;ZSet, score=metric&#41;)

[//]: # ()
[//]: # (### 12.3 검색 / 필터 / 정렬 규칙)

[//]: # (- 서버 측 Set/ZSet 교집합 연산을 우선 사용한다.)

[//]: # (- 파이프라인/멀티키 조회로 네트워크 round-trip을 최소화한다.)

[//]: # (- 키워드 토큰화는 최소 길이 및 기본 불용어 정책을 둔다.)

[//]: # ()
[//]: # (### 12.4 일관성 & 갱신 규칙)

[//]: # (- Product / Price / Stock 변경 시 Catalog Redis를 반드시 갱신한다.)

[//]: # (- 갱신 트리거는 트랜잭션 커밋 이후&#40;AFTER_COMMIT&#41;에 수행한다.)

[//]: # (- Hash와 인덱스 키는 동일 상품 기준으로 일관성 있게 upsert한다.)

[//]: # (- TTL은 Hash에만 적용 가능하며, 인덱스는 장기 유지 또는 배치 재빌드 전략을 둔다.)

[//]: # ()
[//]: # (### 12.5 품질 기준)

[//]: # (- Catalog API는 조회 성능 중심으로 설계한다&#40;페이징/필터/정렬&#41;.)

[//]: # (- Redis 장애 또는 miss 시 동작&#40;fallback/에러&#41;은 문서화하고 테스트로 검증한다.)
