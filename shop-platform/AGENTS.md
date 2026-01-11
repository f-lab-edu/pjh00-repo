# AGENTS.md

> **Canonical Version (English)**  
> The Korean version is a reference translation only.  
> In case of ambiguity, this English version takes precedence.

---

## 1. Project Structure & Module Organization

This repository is a Gradle multi-module workspace following Microservice Architecture (MSA).

- Shared contracts and utilities live in `common/`
- Each API service (e.g. `order-api`, `product-api`) is an independent Spring Boot application
- Infrastructure manifests are placed under `docker-compose/`

Each module follows the standard Spring Boot layout:

- `src/main/java` – application code
- `src/main/resources` – configuration (YAML)
- `src/test/java` – tests

**Rules**
- Service modules MUST NOT depend on other service modules
- Allowed dependencies:
  - `<service>-api` → `common`
  - `common` → (no Spring / no Spring Boot)
- Each service MUST own its database schema and Flyway migrations

---

## 2. Architecture Rules (Hexagonal Architecture)

All API modules MUST strictly follow Hexagonal Architecture.

**Layering Rules**
- Controllers and Kafka listeners MUST be placed under `adapters.in`
- Persistence, Kafka producers, Redis, and external clients MUST be placed under `adapters.out`
- Business logic MUST live in `application` or `domain`
- The application layer MUST depend only on ports, NEVER on adapter implementations
- Domain models MUST NOT depend on Spring, JPA, or infrastructure concerns

**Recommended Package Structure**
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

## 3. Common Module Rules

The `common` module MUST be a **pure Java library**.

**common MUST NOT contain**
- Spring annotations
- Spring Boot dependencies
- JPA entities or repositories
- `@Configuration`, `@Component`, or any framework annotations

**common MAY contain**
- DTOs and API contracts
- Error codes and error response models
- Domain events and event schemas
- Utilities and constants

---

## 4. Database & Data Ownership Rules

- Each service MUST have its own database (schema or physical instance)
- Cross-service database access is STRICTLY FORBIDDEN
- Cross-database joins and foreign keys MUST NOT be used
- IDs from other services are referenced logically only, never via FK

**Soft Delete**
- All deletions MUST use soft delete (`is_deleted`, `deleted_at`)
- Queries for active data MUST filter `is_deleted = 0`
- UNIQUE constraints MUST include `is_deleted` when applicable

---

## 5. Transaction & Outbox Rules

- Transaction boundaries MUST be defined at the application service (use-case) level
- State changes and outbox records MUST be written in the SAME transaction
- Kafka publishing MUST be asynchronous and handled by a separate publisher process
- Outbox records MUST be immutable once created

---

## 6. Messaging & Kafka Rules

**Event Rules**
- Events represent facts that already happened
- Events MUST be immutable
- Events MUST be reprocessable

**Kafka Message Envelope MUST include**
- `eventId`
- `eventType`
- `occurredAt`
- `traceId`
- `producer`
- `schemaVersion`
- Deterministic message key

**Consumer Rules**
- Consumers MUST be idempotent
- Consumers MUST handle duplicate messages safely
- Retry topics and DLQ MUST be used for failures
- Message processing MUST NOT block core business flows

---

## 7. API & Error Handling Rules

- API responses MUST follow a unified response envelope
- Domain exceptions MUST be mapped to common error codes
- `@ControllerAdvice` MUST exist only in `adapters.in.web`
- Request validation MUST occur at adapter level
- Domain invariants MUST be enforced in domain/application layers

---

## 8. Testing Rules

**Testing Strategy**
- JUnit 5 is mandatory
- Prefer test slices:
  - `@DataJpaTest`
  - `@WebMvcTest`
- Use `@SpringBootTest` only for cross-cutting integration flows

**Testcontainers**
- MariaDB, Kafka, and Redis MUST be run via Testcontainers
- Tests MUST NOT depend on external services

**Contracts**
- API contracts SHOULD be validated via OpenAPI or REST Docs
- Kafka event schemas SHOULD have contract tests

---

## 9. Performance & Load Testing

- k6 MUST be used for load testing
- Performance targets MUST match defined SLOs
- Load test scripts SHOULD live under `/performance/k6`

---

## 10. Security & Configuration Rules

- Secrets MUST NOT be committed to the repository
- Configuration MUST be externalized via environment variables
- Sensitive values MUST be injected via `@ConfigurationProperties`
- Logs MUST be structured (JSON)
- PII MUST NOT be logged

---

## 11. Forbidden Practices

- DO NOT use JPA entities as API DTOs
- DO NOT expose entities outside the service boundary
- DO NOT use `RestTemplate` (prefer `WebClient`)
- DO NOT bypass application layer for persistence
- DO NOT introduce circular dependencies

---

## 12. Database Migration Rules (Flyway)

### Flyway Migration Rules

- Flyway MUST be used for all database schema changes
- Direct schema modification outside Flyway is STRICTLY FORBIDDEN
- Migration scripts MUST be immutable once applied

**Naming Convention**
- Versioned migrations MUST follow:
  - `V<version>__<description>.sql`
- Version MUST be monotonically increasing
- Description MUST be lowercase and use underscores

**Examples**
- `V1__init_schema.sql`
- `V2__create_product_table.sql`
- `V3__add_product_indexes.sql`

**Guidelines**
- One migration SHOULD represent one logical change
- Destructive changes (DROP, data migration) MUST be reviewed carefully
- Data backfill migrations SHOULD be separated from schema-only migrations

---

## 13. API Documentation Rules (Swagger / OpenAPI)

### Swagger / OpenAPI Grouping Rules

- All HTTP APIs MUST be documented using OpenAPI (Swagger)
- APIs MUST be grouped by audience using OpenAPI groups
- At minimum, the following groups MUST exist:
  - `public` – customer-facing APIs
  - `admin` – internal or administrative APIs

**Rules**
- Admin APIs MUST NOT be exposed in public API documentation
- Grouping MUST be done via path-based or package-based configuration
- Each group SHOULD have its own title and description
- API documentation MUST reflect actual request/response DTOs (not JPA entities)

**Examples**
- `/api/public/**` → public
- `/api/admin/**` → admin

---

## 14. Codex / LLM Usage Rules

### Codex Usage Rules

When using code-generation tools (e.g. Codex, LLMs):

- This document (AGENTS.md) MUST be treated as a strict specification
- All rules are mandatory unless explicitly stated as optional
- The model MUST NOT infer or introduce new architectural patterns
- The model MUST NOT modify database schemas, API contracts, or event schemas
- The model MUST NOT expose JPA entities outside the service boundary
- If a requirement is ambiguous, the model MUST ask for clarification
- Any output violating this document MUST be considered incorrect

---

[//]: # (## 12. Catalog &#40;Customer Read&#41; Redis Rules – product-api)

[//]: # ()
[//]: # (The Catalog in `product-api` is a **read-only &#40;customer-facing&#41; view** and MUST be backed by Redis.)

[//]: # ()
[//]: # (### 12.1 Core Principles)

[//]: # (- Catalog read APIs MUST use Redis as the primary data source)

[//]: # (- DB fallback behavior MUST be explicitly defined)

[//]: # (  - &#40;Recommended for MVP&#41; Allow DB fallback on Redis miss and populate Redis via read-through)

[//]: # (- Catalog APIs MUST NOT expose JPA entities and MUST return dedicated view/DTO models)

[//]: # ()
[//]: # (### 12.2 Recommended Redis Key Design)

[//]: # (- Product detail:)

[//]: # (  - `catalog:product:{productId}` &#40;Hash&#41;)

[//]: # (- Search indexes:)

[//]: # (  - Keyword token index: `catalog:kw:{token}` &#40;Set of productId&#41;)

[//]: # (  - Category filter: `catalog:category:{categoryId}` &#40;Set of productId&#41;)

[//]: # (  - Price range: `catalog:price` &#40;ZSet, score = price, member = productId&#41;)

[//]: # (  - Newest sort: `catalog:newest` &#40;ZSet, score = createdAtEpoch, member = productId&#41;)

[//]: # (  - &#40;Optional&#41; Popularity sort: `catalog:popularity` &#40;ZSet, score = metric&#41;)

[//]: # ()
[//]: # (### 12.3 Search / Filter / Sort Composition Rules)

[//]: # (- Prefer server-side set/zset intersection where possible)

[//]: # (- Minimize network round-trips using pipelining and multi-key operations)

[//]: # (- Keyword tokenization MUST define minimum token length and basic stop-word rules)

[//]: # ()
[//]: # (### 12.4 Consistency & Update Rules)

[//]: # (- Catalog Redis data MUST be updated on Product / Price / Stock changes)

[//]: # (- Update triggers MUST run AFTER transaction commit)

[//]: # (- Hash and index keys SHOULD be updated in a consistent upsert manner)

[//]: # (- TTL MAY be applied to Hash keys only; index keys SHOULD be long-lived or rebuilt via batch)

[//]: # ()
[//]: # (### 12.5 Quality & Reliability)

[//]: # (- Catalog APIs MUST be optimized for read performance &#40;pagination, filtering, sorting&#41;)

[//]: # (- Redis failure / miss behavior &#40;fallback or error&#41; MUST be documented and covered by tests)

[//]: # (### Flyway 마이그레이션 규칙)

[//]: # ()
[//]: # (- 모든 데이터베이스 스키마 변경은 Flyway를 통해서만 수행해야 한다)

[//]: # (- Flyway 외 수동 DDL 실행은 엄격히 금지된다)

[//]: # (- 한 번 적용된 마이그레이션 파일은 수정할 수 없다)

[//]: # ()
[//]: # (**네이밍 규칙**)

[//]: # (- 버전 마이그레이션 파일은 다음 형식을 따른다:)

[//]: # (  - `V<버전>__<설명>.sql`)

[//]: # (- 버전은 반드시 단조 증가해야 한다)

[//]: # (- 설명은 소문자와 언더스코어&#40;`_`&#41;를 사용한다)

[//]: # ()
[//]: # (**예시**)

[//]: # (- `V1__init_schema.sql`)

[//]: # (- `V2__create_product_table.sql`)

[//]: # (- `V3__add_product_indexes.sql`)

[//]: # ()
[//]: # (**작성 가이드**)

[//]: # (- 하나의 마이그레이션은 하나의 논리적 변경만 포함한다)

[//]: # (- DROP, 데이터 변환과 같은 파괴적 변경은 반드시 리뷰를 거친다)

[//]: # (- 데이터 백필&#40;backfill&#41;은 스키마 변경과 분리한다)

[//]: # ()
[//]: # (---)

[//]: # ()
[//]: # (### Swagger / OpenAPI 그룹 규칙)

[//]: # ()
[//]: # (- 모든 HTTP API는 OpenAPI&#40;Swagger&#41;를 통해 문서화되어야 한다)

[//]: # (- API 문서는 사용자 대상에 따라 그룹화되어야 한다)

[//]: # (- 최소한 다음 두 그룹은 반드시 존재해야 한다:)

[//]: # (  - `public` – 고객 대상 API)

[//]: # (  - `admin` – 내부/관리자 API)

[//]: # ()
[//]: # (**규칙**)

[//]: # (- 관리자 API는 public 문서에 노출되어서는 안 된다)

[//]: # (- 그룹 분리는 path 또는 package 기반 설정을 사용한다)

[//]: # (- 각 그룹은 별도의 제목과 설명을 가져야 한다)

[//]: # (- API 문서는 실제 Request/Response DTO 기준으로 작성한다 &#40;엔티티 금지&#41;)

[//]: # ()
[//]: # (**예시**)

[//]: # (- `/api/public/**` → public)

[//]: # (- `/api/admin/**` → admin)

[//]: # ()
[//]: # (---)

[//]: # ()
[//]: # (### Codex 사용 규칙)

[//]: # ()
[//]: # (코드 생성 도구&#40;Codex, LLM 등&#41;를 사용할 때:)

[//]: # ()
[//]: # (- AGENTS.md는 엄격한 명세&#40;specification&#41;로 취급해야 한다)

[//]: # (- 선택 사항이라고 명시되지 않은 모든 규칙은 필수다)

[//]: # (- 모델은 새로운 아키텍처 패턴을 임의로 도입해서는 안 된다)

[//]: # (- DB 스키마, API 계약, 이벤트 스키마를 변경해서는 안 된다)

[//]: # (- JPA 엔티티를 서비스 외부로 노출해서는 안 된다)

[//]: # (- 요구사항이 모호한 경우 반드시 질문해야 한다)

[//]: # (- 본 문서를 위반한 결과물은 잘못된 출력으로 간주한다)