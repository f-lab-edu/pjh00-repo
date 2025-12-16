CREATE DATABASE IF NOT EXISTS order_db DEFAULT CHARACTER SET utf8mb4;
USE order_db;

CREATE TABLE orders (
                        id            BIGINT PRIMARY KEY AUTO_INCREMENT,
                        order_no      VARCHAR(64) NOT NULL UNIQUE,
                        user_id       BIGINT NOT NULL,
                        status        VARCHAR(32) NOT NULL,  -- CREATED, PAID, CANCELED, FAILED
                        total_amount  BIGINT NOT NULL,
                        created_at    DATETIME(6) NOT NULL,
                        updated_at    DATETIME(6) NOT NULL
) ENGINE=InnoDB;

CREATE TABLE order_item (
                            id            BIGINT PRIMARY KEY AUTO_INCREMENT,
                            order_id      BIGINT NOT NULL,
                            product_id    BIGINT NOT NULL,       -- 다른 서비스의 ID지만 FK 걸지 않음(경계 유지)
                            sku           VARCHAR(64) NOT NULL,   -- 주문 시점 스냅샷
                            product_name  VARCHAR(255) NOT NULL,
                            unit_price    BIGINT NOT NULL,
                            quantity      INT NOT NULL,
                            created_at    DATETIME(6) NOT NULL,
                            CONSTRAINT fk_item_order FOREIGN KEY (order_id) REFERENCES orders(id)
) ENGINE=InnoDB;
