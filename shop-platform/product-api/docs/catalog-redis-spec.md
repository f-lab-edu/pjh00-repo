# Product Catalog Redis Spec

## 1. 목적
- 고객-facing 카탈로그 트래픽을 DB에 의존하지 않고 Redis에서 소화한다.
- 메인/카테고리별/랭킹 리스트와 단일 상품 뷰를 Redis에서 조회하고, 키워드 검색은 Elasticsearch가 기본이나 Redis 토큰 인덱스로 간단한 매칭을 제공한다.

## 2. 데이터 모델 (DTO)
| DTO | 설명 |
| --- | --- |
| `CatalogProductView` | 상세 조회 응답. 브랜드/카테고리 컨텍스트, 가격/재고, 뱃지/하이라이트/이미지 포함 |
| `CatalogSearchEntry` | 목록/검색 결과에서 사용하는 요약 카드 |
| `PriceSummary` | 기본가/통화/할인가 정보. `hasDiscount()` 헬퍼 제공 |
| `InventorySummary` | 재고 수량 + 재고 여부 판별 |
| `CatalogSearchQuery` | 키워드, 카테고리, 가격 범위, 브랜드 필터, 정렬, 페이지/사이즈 |
| `CatalogSearchResponse` | 검색 결과 목록 + totalCount/page/size |

## 3. Redis Key 설계
| 키 | 타입 | 내용 |
| --- | --- | --- |
| `catalog:product:{productId}` | Hash | `product:name`, `brand:id`, `brand:name`, `category:id`, `category:path`, `price:base`, `price:currency`, `price:discounted`, `inventory:stock`, `inventory:in_stock`, `product:badges`, `product:highlights`, `media:images`, `media:thumbnail`, `popularity:score`, `timestamps:updated` |
| `catalog:category:{categoryId}:{sort}` | ZSet | member: `productId`, score는 정렬 기준(`sort ∈ {relevance, popular, price:asc, price:desc, new}`) |
| `catalog:ranking:{bucket}` | ZSet | `bucket ∈ {relevance, popular, price:asc, price:desc, newest}`. score는 랭킹 기준 |
| `catalog:kw:{token}:{sort}` | Set/ZSet | 키워드 토큰 index. 간단 검색용. 고급 검색은 Elasticsearch에서 처리 |
| `catalog:brand:{brandId}` | ZSet | 브랜드 전용 목록(선택) |

TTL은 Hash(단일 상품)는 선택적으로 24~48h 부여, 목록/랭킹 키는 상시 유지 후 배치로 재생성.

## 4. 쓰기 플로우
1. 상품/가격/재고 변경 시 트랜잭션 내부에서 아웃박스 `product_catalog_outbox`에 변경 이벤트 기록.
2. Kafka 퍼블리셔가 아웃박스를 폴링해 `product-catalog-events` 토픽으로 발행.
3. Redis 컨슈머가 이벤트를 읽어 Hash/인덱스 키를 upsert/삭제.
4. Elasticsearch 인덱스 컨슈머도 같은 이벤트를 구독해 검색 인덱스를 업데이트.

모든 업데이트는 도메인 트랜잭션 커밋 이후 비동기로 실행하며, 실패 시 재시도 + DLQ.

### 4.1 Outbox 스키마
테이블: `product_catalog_outbox`

| 컬럼 | 타입 | 설명 |
| --- | --- | --- |
| `id` | BIGINT | 아웃박스 PK (시간 기반 ID) |
| `product_id` | BIGINT | 대상 상품 ID |
| `event_type` | VARCHAR(50) | `PRODUCT_CREATED`, `PRODUCT_UPDATED`, `PRODUCT_DELETED`, `PRICE_CHANGED`, `STOCK_CHANGED` |
| `payload` | JSON | 이벤트별 데이터 (상품 스냅샷, 가격/재고 old-new 값 등) |
| `occurred_at` | DATETIME(6) | 이벤트 발생 시간 (UTC) |
| `processed` | TINYINT(1) | 소비 여부 (기본 false) |
| `processed_at` | DATETIME(6) | 처리 시각 (nullable) |
| `created_at` | DATETIME(6) | 레코드 생성 시각 |

payload 예시

**상품 스냅샷 (CREATED/UPDATED/DELETED)**
```json
{
  "productId": 5001,
  "sellerId": 7001,
  "brandId": 100,
  "categoryId": 210005,
  "name": "프리미엄 전자 공기청정기",
  "status": "ACTIVE",
  "basePrice": 129000,
  "currency": "KRW",
  "stockQuantity": 45,
  "updatedAt": "2026-01-12T01:40:14",
  "deletedAt": null
}
```

**가격 변경 (PRICE_CHANGED)**
```json
{
  "productId": 5001,
  "oldPrice": 129000,
  "newPrice": 119000,
  "currency": "KRW"
}
```

**재고 변경 (STOCK_CHANGED)**
```json
{
  "productId": 5001,
  "oldStock": 30,
  "newStock": 55
}
```

## 5. 조회 플로우
- `CatalogQueryUseCase.getProductDetail(productId)` → Redis Hash 조회 실패 시 DB/Elasticsearch 폴백 후 write-through.
- `searchCatalog(query)` → 기본은 Elasticsearch 결과. Redis 토큰 인덱스는 짧은 키워드·랭킹 페이지 최적화용.
- 메인/카테고리/랭킹 API는 Redis ZSet 기반으로 즉시 응답.

## 6. 패키지/포트 구조
- `application.port.dto` : 위 DTO 레코드 (어댑터/서비스 공용)
- `CatalogQueryUseCase` : DTO 기반 계약 제공
- `CatalogReadPort` : Redis 어댑터를 통한 조회 포트 (`fetchProductView`, `fetchCatalogEntries`, `fetchProductSummaries`)
- `RedisCatalogReadAdapter` : `StringRedisTemplate` 기반 조회 어댑터 (키 계산은 `CatalogRedisKeyFactory`)

## 7. TODO
- 아웃박스 스키마/이벤트 스키마 정의 (`product_id, event_type, payload, occurred_at`)
- Kafka consumer + Redis writer 구현 (`redis-02-catalog-adapter` 브랜치에서 진행 예정)
- Elasticsearch 인덱스 설계 및 contract 테스트
- Testcontainers 기반 Redis/E2E 테스트, k6 부하 스크립트
