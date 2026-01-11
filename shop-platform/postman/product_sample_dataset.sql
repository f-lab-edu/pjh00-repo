-- -----------------------------------------------------------------------------
-- Bulk dataset builder for product-api (~2,000,000 products) with realistic text
-- -----------------------------------------------------------------------------
-- 이 스크립트는 product-api가 소유한 테이블을 전부 비우고 쿠팡/아마존 스타일의
-- 전자상거래 데이터를 대량으로 주입합니다. 캐시·검색·로드 테스트 용도로만
-- 사용하세요. 공유 DB나 운영 환경에서는 절대로 실행하지 마세요.
-- -----------------------------------------------------------------------------

SET autocommit = 0;
SET FOREIGN_KEY_CHECKS = 0;

TRUNCATE TABLE product_stock_history;
TRUNCATE TABLE product_price_history;
TRUNCATE TABLE product;
TRUNCATE TABLE category_closure;
TRUNCATE TABLE category;
TRUNCATE TABLE brand;

SET FOREIGN_KEY_CHECKS = 1;

-- -----------------------------------------------------------------------------
-- 공통 시퀀스 / 유틸 테이블
-- -----------------------------------------------------------------------------
DROP TEMPORARY TABLE IF EXISTS tmp_digits;
CREATE TEMPORARY TABLE tmp_digits (
    d TINYINT UNSIGNED NOT NULL,
    PRIMARY KEY (d)
) ENGINE = Memory;
INSERT INTO tmp_digits(d) VALUES (0),(1),(2),(3),(4),(5),(6),(7),(8),(9);

DROP TEMPORARY TABLE IF EXISTS tmp_1k;
CREATE TEMPORARY TABLE tmp_1k (
    n INT UNSIGNED NOT NULL,
    PRIMARY KEY (n)
) ENGINE = Memory;
INSERT INTO tmp_1k (n)
SELECT d0.d + d1.d * 10 + d2.d * 100
FROM tmp_digits d0
CROSS JOIN tmp_digits d1
CROSS JOIN tmp_digits d2;

DROP TEMPORARY TABLE IF EXISTS tmp_product_seq;
CREATE TEMPORARY TABLE tmp_product_seq (
    seq BIGINT NOT NULL,
    PRIMARY KEY (seq)
) ENGINE = InnoDB;
INSERT INTO tmp_product_seq (seq)
SELECT a.n + b.n * 1000 + million.m * 1000000 AS seq
FROM tmp_1k a
CROSS JOIN tmp_1k b
CROSS JOIN (SELECT 0 AS m UNION ALL SELECT 1) million
WHERE a.n + b.n * 1000 + million.m * 1000000 < 2000000;

DROP TEMPORARY TABLE IF EXISTS seed_pair;
CREATE TEMPORARY TABLE seed_pair (
    factor TINYINT NOT NULL,
    PRIMARY KEY (factor)
) ENGINE = Memory;
INSERT INTO seed_pair VALUES (0),(1);

-- -----------------------------------------------------------------------------
-- 사전 데이터 (브랜드/제품명/카테고리 템플릿)
-- -----------------------------------------------------------------------------
DROP TEMPORARY TABLE IF EXISTS tmp_brand_prefix;
CREATE TEMPORARY TABLE tmp_brand_prefix (
    id TINYINT UNSIGNED PRIMARY KEY,
    word VARCHAR(40) NOT NULL
) ENGINE = Memory;
INSERT INTO tmp_brand_prefix(id, word) VALUES
    (1,'한빛'),(2,'라온'),(3,'누리'),(4,'하랑'),(5,'다온'),(6,'모아'),
    (7,'소담'),(8,'비움'),(9,'오름'),(10,'청아'),(11,'마루'),(12,'온새미'),
    (13,'새결'),(14,'해밀'),(15,'다담'),(16,'채움');

DROP TEMPORARY TABLE IF EXISTS tmp_brand_suffix;
CREATE TEMPORARY TABLE tmp_brand_suffix (
    id TINYINT UNSIGNED PRIMARY KEY,
    word VARCHAR(40) NOT NULL
) ENGINE = Memory;
INSERT INTO tmp_brand_suffix(id, word) VALUES
    (1,'상회'),(2,'라이프'),(3,'리빙'),(4,'마켓'),(5,'컴퍼니'),(6,'상사'),
    (7,'상단'),(8,'팩토리'),(9,'공작소'),(10,'스튜디오'),(11,'연합'),(12,'제작소');

DROP TEMPORARY TABLE IF EXISTS tmp_product_adj;
CREATE TEMPORARY TABLE tmp_product_adj (
    id TINYINT UNSIGNED PRIMARY KEY,
    word VARCHAR(40) NOT NULL
) ENGINE = Memory;
INSERT INTO tmp_product_adj(id, word) VALUES
    (1,'프리미엄'),(2,'스마트'),(3,'울트라'),(4,'에코'),(5,'컴팩트'),(6,'라이트'),
    (7,'튼튼한'),(8,'무선'),(9,'장인'),(10,'헤리티지'),(11,'미니멀'),(12,'퍼포먼스');

DROP TEMPORARY TABLE IF EXISTS tmp_product_noun;
CREATE TEMPORARY TABLE tmp_product_noun (
    id TINYINT UNSIGNED PRIMARY KEY,
    word VARCHAR(60) NOT NULL
) ENGINE = Memory;
INSERT INTO tmp_product_noun(id, word) VALUES
    (1,'공기청정기'),(2,'러닝화'),(3,'앰플'),(4,'쿡웨어세트'),(5,'백팩'),
    (6,'커피그라인더'),(7,'무선이어버드'),(8,'책상의자'),(9,'트레일자켓'),
    (10,'베이비모니터'),(11,'펫급식기'),(12,'게이밍키보드');

DROP TEMPORARY TABLE IF EXISTS tmp_product_feature;
CREATE TEMPORARY TABLE tmp_product_feature (
    id TINYINT UNSIGNED PRIMARY KEY,
    phrase VARCHAR(100) NOT NULL
) ENGINE = Memory;
INSERT INTO tmp_product_feature(id, phrase) VALUES
    (1,'다중 필터 구조'),(2,'통기성 니트 어퍼'),(3,'피부과 테스트 유효성분'),
    (4,'3중 스테인리스 코어'),(5,'충격 완화형 스트랩'),(6,'정밀 버 그라인딩'),
    (7,'저지연 사운드'),(8,'인체공학 요추 지지'),(9,'방수 방풍 쉘'),
    (10,'AI 알림 시스템'),(11,'예약 급식 시나리오'),(12,'핫스와프 스위치');

DROP TEMPORARY TABLE IF EXISTS tmp_product_benefit;
CREATE TEMPORARY TABLE tmp_product_benefit (
    id TINYINT UNSIGNED PRIMARY KEY,
    phrase VARCHAR(100) NOT NULL
) ENGINE = Memory;
INSERT INTO tmp_product_benefit(id, phrase) VALUES
    (1,'일상 케어'),(2,'장시간 근무'),(3,'주말 아웃도어'),(4,'가족 생활'),
    (5,'홈 스튜디오 작업'),(6,'미니멀 주방'),(7,'반려동물 케어'),(8,'모바일 게이머'),
    (9,'친환경 소비'),(10,'초보 부모'),(11,'출퇴근 이동'),(12,'홈 카페 취향');

DROP TEMPORARY TABLE IF EXISTS tmp_leaf_descriptor;
CREATE TEMPORARY TABLE tmp_leaf_descriptor (
    id TINYINT UNSIGNED PRIMARY KEY,
    tag VARCHAR(60) NOT NULL
) ENGINE = Memory;
INSERT INTO tmp_leaf_descriptor(id, tag) VALUES
    (1,'필수관'),(2,'컬렉션'),(3,'아울렛'),(4,'스튜디오'),(5,'기어존'),(6,'즐겨찾기'),
    (7,'큐레이션'),(8,'셀렉트'),(9,'허브'),(10,'바자'),(11,'에디션'),(12,'갤러리'),
    (13,'포커스'),(14,'디포'),(15,'마켓');

DROP TEMPORARY TABLE IF EXISTS tmp_root_def;
CREATE TEMPORARY TABLE tmp_root_def (
    idx TINYINT UNSIGNED PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    leaf_prefix VARCHAR(100) NOT NULL,
    price_min INT NOT NULL,
    price_max INT NOT NULL,
    stock_min INT NOT NULL,
    stock_max INT NOT NULL
) ENGINE = Memory;
INSERT INTO tmp_root_def(idx, name, leaf_prefix, price_min, price_max, stock_min, stock_max) VALUES
    (1,'전자제품','전자',25000,1800000,5,120),
    (2,'리빙','리빙',15000,500000,10,300),
    (3,'패션','패션',10000,400000,20,600),
    (4,'뷰티·퍼스널케어','뷰티',5000,250000,15,500),
    (5,'식품·간편식','푸드',1000,150000,30,800),
    (6,'스포츠·아웃도어','스포츠',8000,600000,10,450),
    (7,'유아·키즈','키즈',5000,400000,20,700),
    (8,'자동차·모빌리티','오토',12000,1200000,5,200),
    (9,'반려동물','펫',5000,200000,10,350),
    (10,'도서·미디어','미디어',2000,150000,5,220),
    (11,'건강·웰니스','헬스',5000,300000,10,400),
    (12,'오피스·문구','오피스',3000,250000,15,550),
    (13,'여행·러기지','트래블',10000,800000,8,260),
    (14,'가구·데코','가구',30000,2000000,2,120),
    (15,'DIY·공구','DIY',8000,900000,5,260),
    (16,'게임·엔터','게임',15000,900000,3,180),
    (17,'럭셔리·주얼리','럭셔리',50000,5000000,1,80),
    (18,'디지털가전','디지털',20000,2500000,4,200),
    (19,'선물세트·베버리지','선물',5000,300000,8,220),
    (20,'친환경·지속가능','에코',7000,450000,6,240);

SET @seed_now := NOW(6);
SET @brand_base_id := 100000;
SET @brand_count := 5000;
SET @root_category_base_id := 200000;
SET @leaf_category_base_id := 210000;
SET @leafs_per_root := 60;
SET @product_limit := 2000000;
SET @seller_pool := 80000;
SET @product_base_id := 500000000000;
SET @price_history_base_id := 700000000000;
SET @stock_history_base_id := 900000000000;

SET @brand_prefix_count := (SELECT COUNT(*) FROM tmp_brand_prefix);
SET @brand_suffix_count := (SELECT COUNT(*) FROM tmp_brand_suffix);
SET @product_adj_count := (SELECT COUNT(*) FROM tmp_product_adj);
SET @product_noun_count := (SELECT COUNT(*) FROM tmp_product_noun);
SET @product_feature_count := (SELECT COUNT(*) FROM tmp_product_feature);
SET @product_benefit_count := (SELECT COUNT(*) FROM tmp_product_benefit);
SET @leaf_descriptor_count := (SELECT COUNT(*) FROM tmp_leaf_descriptor);
SET @root_category_count := (SELECT COUNT(*) FROM tmp_root_def);
SET @leaf_category_count := @root_category_count * @leafs_per_root;

-- -----------------------------------------------------------------------------
-- Brands (5,000 realistic labels)
-- -----------------------------------------------------------------------------
SET @brand_row := 0;
INSERT INTO brand (id, name, deleted_at, created_at, updated_at)
SELECT
    @brand_base_id + row_no AS id,
    CONCAT(bp.word, ' ', bs.word, ' ', LPAD(MOD(row_no, 999) + 1, 3, '0')) AS name,
    NULL,
    DATE_SUB(@seed_now, INTERVAL MOD(row_no, 400) DAY) AS created_at,
    DATE_ADD(DATE_SUB(@seed_now, INTERVAL MOD(row_no, 200) DAY), INTERVAL MOD(row_no, 30) DAY) AS updated_at
FROM (
    SELECT @brand_row := @brand_row + 1 AS row_no
    FROM tmp_product_seq
    WHERE seq < @brand_count
) seq_data
JOIN tmp_brand_prefix bp ON bp.id = MOD(seq_data.row_no - 1, @brand_prefix_count) + 1
JOIN tmp_brand_suffix bs ON bs.id = MOD(seq_data.row_no - 1, @brand_suffix_count) + 1;

-- -----------------------------------------------------------------------------
-- Categories (root + leaf) with metadata
-- -----------------------------------------------------------------------------
SET @root_row := 0;
INSERT INTO category (id, parent_id, name, depth, is_active, deleted_at, created_at, updated_at)
SELECT
    @root_category_base_id + row_no AS id,
    NULL,
    name,
    0,
    1,
    NULL,
    DATE_SUB(@seed_now, INTERVAL MOD(row_no, 200) DAY) AS created_at,
    DATE_ADD(DATE_SUB(@seed_now, INTERVAL MOD(row_no, 100) DAY), INTERVAL MOD(row_no, 15) DAY) AS updated_at
FROM (
    SELECT @root_row := @root_row + 1 AS row_no, def.*
    FROM tmp_root_def def
    ORDER BY def.idx
) root_seq;

DROP TEMPORARY TABLE IF EXISTS tmp_root_profile;
CREATE TEMPORARY TABLE tmp_root_profile (
    root_id BIGINT PRIMARY KEY,
    root_name VARCHAR(100) NOT NULL,
    leaf_prefix VARCHAR(100) NOT NULL,
    price_min INT NOT NULL,
    price_max INT NOT NULL,
    stock_min INT NOT NULL,
    stock_max INT NOT NULL
) ENGINE = Memory;
INSERT INTO tmp_root_profile(root_id, root_name, leaf_prefix, price_min, price_max, stock_min, stock_max)
SELECT c.id, c.name, def.leaf_prefix, def.price_min, def.price_max, def.stock_min, def.stock_max
FROM category c
JOIN tmp_root_def def ON def.name = c.name
WHERE c.parent_id IS NULL;

DROP TEMPORARY TABLE IF EXISTS tmp_leaf_rows;
CREATE TEMPORARY TABLE tmp_leaf_rows (
    row_no INT UNSIGNED NOT NULL,
    category_id BIGINT NOT NULL,
    parent_id BIGINT NOT NULL,
    name VARCHAR(200) NOT NULL,
    descriptor VARCHAR(100) NOT NULL,
    price_min INT NOT NULL,
    price_max INT NOT NULL,
    stock_min INT NOT NULL,
    stock_max INT NOT NULL,
    PRIMARY KEY (category_id)
) ENGINE = Memory;

SET @leaf_row := 0;
INSERT INTO tmp_leaf_rows
SELECT
    row_no,
    @leaf_category_base_id + row_no AS category_id,
    parent_id,
    leaf_name,
    descriptor,
    price_min,
    price_max,
    stock_min,
    stock_max
FROM (
    SELECT
        (@leaf_row := @leaf_row + 1) AS row_no,
        rp.root_id AS parent_id,
        CONCAT(rp.leaf_prefix, ' ', ld.tag, ' ', LPAD(ls.n + 1, 2, '0')) AS leaf_name,
        ld.tag AS descriptor,
        GREATEST(rp.price_min + MOD(ls.n * 131, GREATEST(rp.price_max - rp.price_min - 2000, 1000)), rp.price_min + 500) AS price_min,
        rp.price_max - MOD(ls.n * 73, 1800) AS price_max,
        rp.stock_min + MOD(ls.n * 17, GREATEST(rp.stock_max - rp.stock_min, 20)) AS stock_min,
        rp.stock_max - MOD(ls.n * 7, 30) AS stock_max
    FROM tmp_root_profile rp
    JOIN (SELECT n FROM tmp_1k WHERE n < @leafs_per_root) ls ON 1 = 1
    JOIN tmp_leaf_descriptor ld ON ld.id = MOD(ls.n, @leaf_descriptor_count) + 1
    ORDER BY rp.root_id, ls.n
) leaf_data;

INSERT INTO category (id, parent_id, name, depth, is_active, deleted_at, created_at, updated_at)
SELECT
    category_id,
    parent_id,
    name,
    1,
    1,
    NULL,
    DATE_SUB(@seed_now, INTERVAL MOD(row_no, 120) DAY) AS created_at,
    DATE_ADD(DATE_SUB(@seed_now, INTERVAL MOD(row_no, 60) DAY), INTERVAL MOD(row_no, 10) DAY) AS updated_at
FROM tmp_leaf_rows
ORDER BY category_id;

INSERT INTO category_closure (ancestor_id, descendant_id, depth)
SELECT id, id, 0 FROM category;
INSERT INTO category_closure (ancestor_id, descendant_id, depth)
SELECT parent_id, id, 1 FROM category WHERE parent_id IS NOT NULL;

DROP TEMPORARY TABLE IF EXISTS tmp_leaf_profile;
CREATE TEMPORARY TABLE tmp_leaf_profile (
    category_id BIGINT PRIMARY KEY,
    parent_id BIGINT NOT NULL,
    root_name VARCHAR(100) NOT NULL,
    descriptor VARCHAR(100) NOT NULL,
    price_min INT NOT NULL,
    price_max INT NOT NULL,
    stock_min INT NOT NULL,
    stock_max INT NOT NULL,
    seq_no INT NOT NULL
) ENGINE = Memory;

SET @leaf_profile_row := 0;
INSERT INTO tmp_leaf_profile(category_id, parent_id, root_name, descriptor, price_min, price_max, stock_min, stock_max, seq_no)
SELECT
    lr.category_id,
    lr.parent_id,
    rp.root_name,
    lr.descriptor,
    lr.price_min,
    lr.price_max,
    lr.stock_min,
    lr.stock_max,
    (@leaf_profile_row := @leaf_profile_row + 1) AS seq_no
FROM tmp_leaf_rows lr
JOIN tmp_root_profile rp ON rp.root_id = lr.parent_id
ORDER BY lr.category_id;

-- -----------------------------------------------------------------------------
-- Products (2,000,000 rows) with varied naming/price/stock
-- -----------------------------------------------------------------------------
INSERT INTO product (
    id,
    seller_id,
    brand_id,
    category_id,
    name,
    description,
    status,
    base_price,
    currency,
    stock_quantity,
    popularity_score,
    deleted_at,
    created_at,
    updated_at
)
SELECT
    @product_base_id + seq.seq + 1 AS id,
    400000 + MOD(seq.seq, @seller_pool) + 1 AS seller_id,
    @brand_base_id + MOD(seq.seq, @brand_count) + 1 AS brand_id,
    leaf.category_id,
    CONCAT(padj.word, ' ', leaf.root_name, ' ', pnoun.word, ' ', LPAD(MOD(seq.seq, 9999) + 1, 4, '0')) AS name,
    CONCAT(leaf.root_name, ' 취향 고객을 위한 ', padj.word, ' ', pnoun.word,
           '로 ', pfeature.phrase, ' 설계를 적용해 ', pbenefit.phrase, '을 돕습니다.') AS description,
    CASE
        WHEN MOD(seq.seq, 20) < 12 THEN 'ACTIVE'
        WHEN MOD(seq.seq, 20) < 17 THEN 'INACTIVE'
        ELSE 'DRAFT'
    END AS status,
    leaf.price_min + MOD(seq.seq * 97, GREATEST(leaf.price_max - leaf.price_min, 1)) AS base_price,
    'KRW' AS currency,
    leaf.stock_min + MOD(seq.seq * 11, GREATEST(leaf.stock_max - leaf.stock_min, 1)) AS stock_quantity,
    MOD(seq.seq * 13 + leaf.stock_max, 100000) AS popularity_score,
    NULL,
    DATE_SUB(@seed_now, INTERVAL MOD(seq.seq, 365) DAY) AS created_at,
    DATE_ADD(
        DATE_SUB(@seed_now, INTERVAL MOD(seq.seq, 365) DAY),
        INTERVAL MOD(seq.seq, 30) DAY
    ) AS updated_at
FROM tmp_product_seq seq
JOIN tmp_leaf_profile leaf ON leaf.seq_no = MOD(seq.seq, @leaf_category_count) + 1
JOIN tmp_product_adj padj ON padj.id = MOD(seq.seq, @product_adj_count) + 1
JOIN tmp_product_noun pnoun ON pnoun.id = MOD(seq.seq, @product_noun_count) + 1
JOIN tmp_product_feature pfeature ON pfeature.id = MOD(seq.seq, @product_feature_count) + 1
JOIN tmp_product_benefit pbenefit ON pbenefit.id = MOD(seq.seq, @product_benefit_count) + 1
WHERE seq.seq < @product_limit
ORDER BY seq.seq;

-- -----------------------------------------------------------------------------
-- Price history (two events per product with narrative reasons)
-- -----------------------------------------------------------------------------
INSERT INTO product_price_history (
    id,
    product_id,
    old_price,
    new_price,
    reason,
    created_at
)
SELECT
    @price_history_base_id + seq.seq * 2 + pair.factor + 1 AS id,
    @product_base_id + seq.seq + 1 AS product_id,
    GREATEST(leaf.price_min, leaf.price_min + MOD(seq.seq * 61, 4000) - pair.factor * 500) AS old_price,
    LEAST(leaf.price_max, leaf.price_min + MOD(seq.seq * 89, 6000) + (pair.factor + 1) * 800) AS new_price,
    CASE MOD(seq.seq + pair.factor, 4)
        WHEN 0 THEN '첫 등록가'
        WHEN 1 THEN '프로모션 조정'
        WHEN 2 THEN '공급가 인상'
        ELSE '시즌 번들'
    END AS reason,
    DATE_ADD(
        DATE_SUB(@seed_now, INTERVAL MOD(seq.seq, 365) DAY),
        INTERVAL pair.factor * 21 DAY
    ) AS created_at
FROM tmp_product_seq seq
JOIN seed_pair pair ON 1 = 1
JOIN tmp_leaf_profile leaf ON leaf.seq_no = MOD(seq.seq, @leaf_category_count) + 1
WHERE seq.seq < @product_limit;

-- -----------------------------------------------------------------------------
-- Stock history (two events per product with warehouse reasons)
-- -----------------------------------------------------------------------------
INSERT INTO product_stock_history (
    id,
    product_id,
    old_stock,
    new_stock,
    reason,
    created_at
)
SELECT
    @stock_history_base_id + seq.seq * 2 + pair.factor + 1 AS id,
    @product_base_id + seq.seq + 1 AS product_id,
    GREATEST(0, leaf.stock_min + MOD(seq.seq * 19, 200) - pair.factor * 15) AS old_stock,
    LEAST(leaf.stock_max, leaf.stock_min + MOD(seq.seq * 23, 220) + pair.factor * 35) AS new_stock,
    CASE MOD(seq.seq + pair.factor, 3)
        WHEN 0 THEN '초기 재고'
        WHEN 1 THEN '물류센터 입고'
        ELSE '특가 복구'
    END AS reason,
    DATE_ADD(
        DATE_SUB(@seed_now, INTERVAL MOD(seq.seq, 300) DAY),
        INTERVAL pair.factor * 14 DAY
    ) AS created_at
FROM tmp_product_seq seq
JOIN seed_pair pair ON 1 = 1
JOIN tmp_leaf_profile leaf ON leaf.seq_no = MOD(seq.seq, @leaf_category_count) + 1
WHERE seq.seq < @product_limit;

COMMIT;

-- -----------------------------------------------------------------------------
-- Cleanup temporary structures
-- -----------------------------------------------------------------------------
DROP TEMPORARY TABLE IF EXISTS tmp_leaf_profile;
DROP TEMPORARY TABLE IF EXISTS tmp_leaf_rows;
DROP TEMPORARY TABLE IF EXISTS tmp_root_profile;
DROP TEMPORARY TABLE IF EXISTS tmp_root_def;
DROP TEMPORARY TABLE IF EXISTS tmp_leaf_descriptor;
DROP TEMPORARY TABLE IF EXISTS tmp_product_benefit;
DROP TEMPORARY TABLE IF EXISTS tmp_product_feature;
DROP TEMPORARY TABLE IF EXISTS tmp_product_noun;
DROP TEMPORARY TABLE IF EXISTS tmp_product_adj;
DROP TEMPORARY TABLE IF EXISTS tmp_brand_suffix;
DROP TEMPORARY TABLE IF EXISTS tmp_brand_prefix;
DROP TEMPORARY TABLE IF EXISTS seed_pair;
DROP TEMPORARY TABLE IF EXISTS tmp_product_seq;
DROP TEMPORARY TABLE IF EXISTS tmp_1k;
DROP TEMPORARY TABLE IF EXISTS tmp_digits;
