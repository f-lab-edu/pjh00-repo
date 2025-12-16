# shop-platform

`shop-platform`은 **모노레포 기반의 MSA**로 운영되는 온라인 쇼핑몰 백엔드 프로젝트입니다.  
서비스는 도메인 단위로 분리되어 **독립 실행/배포** 가능하며, 각 서비스 내부는 **Hexagonal Architecture(Ports & Adapters)**로 설계했습니다.

## Architecture

- **Runtime(운영) 관점**: MSA (Product / Order 서비스 독립 실행)
- **Repo(코드 관리) 관점**: Monorepo (한 저장소에 여러 서비스 공존)
- **Service Internal(내부 구조)**: Hexagonal Architecture
- **Communication**: HTTP + Kafka 이벤트
- **Data**: MariaDB, Redis
- **Observability**: Actuator + Prometheus(Micrometer)

## Modules

- `product-api`: 상품 도메인 서비스 (Spring Boot 실행 모듈)
- `order-api`: 주문 도메인 서비스 (Spring Boot 실행 모듈)
- `common`: 공통 규약/유틸 모듈 (응답 포맷, 에러 규약, 로깅/관측 유틸, 이벤트 계약 등)

> NOTE: `common`에는 비즈니스 도메인 엔티티(JPA Entity)나 도메인 로직을 두지 않고, 공유해도 안전한 계약/유틸만 유지합니다.

## Prerequisites

- Java 21
- Docker Desktop
- Gradle Wrapper

## Local Infrastructure (Docker)

컨테이너 포트 매핑(로컬 기준):
- MariaDB: `localhost:13306`
- Redis: `localhost:16379`
- Kafka: `localhost:19094` (EXTERNAL listener)

### Start
```bash
docker compose -f docker-compose/maria.yml up -d
docker compose -f docker-compose/redis.yml up -d
docker compose -f docker-compose/kafka.yml up -d
