CREATE TABLE IF NOT EXISTS product_catalog_outbox (
    id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    event_type VARCHAR(50) NOT NULL,
    payload JSON NOT NULL,
    occurred_at DATETIME(6) NOT NULL,
    processed TINYINT(1) NOT NULL DEFAULT 0,
    processed_at DATETIME(6) NULL,
    created_at DATETIME(6) NOT NULL,
    PRIMARY KEY (id),
    KEY idx_product_catalog_outbox_processed (processed, occurred_at),
    KEY idx_product_catalog_outbox_product (product_id)
) ENGINE = InnoDB;
