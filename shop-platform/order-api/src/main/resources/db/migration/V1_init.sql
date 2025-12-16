CREATE DATABASE IF NOT EXISTS product_db DEFAULT CHARACTER SET utf8mb4;
USE product_db;

CREATE TABLE product (
                         id            BIGINT PRIMARY KEY AUTO_INCREMENT,
                         sku           VARCHAR(64) NOT NULL UNIQUE,
                         name          VARCHAR(255) NOT NULL,
                         description   TEXT NULL,
                         price         BIGINT NOT NULL,
                         status        VARCHAR(32) NOT NULL,  -- ACTIVE, INACTIVE, SOLD_OUT 등
                         created_at    DATETIME(6) NOT NULL,
                         updated_at    DATETIME(6) NOT NULL
) ENGINE=InnoDB;

CREATE TABLE product_stock (
                               product_id    BIGINT PRIMARY KEY,
                               quantity      INT NOT NULL,
                               updated_at    DATETIME(6) NOT NULL,
                               CONSTRAINT fk_stock_product FOREIGN KEY (product_id) REFERENCES product(id)
) ENGINE=InnoDB;
