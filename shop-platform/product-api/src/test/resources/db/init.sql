CREATE TABLE IF NOT EXISTS brand (
  id BIGINT NOT NULL,
  name VARCHAR(100) NOT NULL,
  deleted_at DATETIME(6) NULL,
  created_at DATETIME(6) NOT NULL,
  updated_at DATETIME(6) NOT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uk_brand_name (name)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS category (
  id BIGINT NOT NULL,
  parent_id BIGINT NULL,
  name VARCHAR(100) NOT NULL,
  depth INT NOT NULL,
  is_active TINYINT(1) NOT NULL DEFAULT 1,
  deleted_at DATETIME(6) NULL,
  created_at DATETIME(6) NOT NULL,
  updated_at DATETIME(6) NOT NULL,
  PRIMARY KEY (id),
  KEY idx_category_parent (parent_id),
  CONSTRAINT fk_category_parent FOREIGN KEY (parent_id) REFERENCES category(id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS category_closure (
  ancestor_id BIGINT NOT NULL,
  descendant_id BIGINT NOT NULL,
  depth INT NOT NULL,
  PRIMARY KEY (ancestor_id, descendant_id),
  KEY idx_cc_descendant (descendant_id),
  CONSTRAINT fk_cc_ancestor FOREIGN KEY (ancestor_id) REFERENCES category(id),
  CONSTRAINT fk_cc_descendant FOREIGN KEY (descendant_id) REFERENCES category(id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS product (
  id BIGINT NOT NULL,
  seller_id BIGINT NOT NULL,
  brand_id BIGINT NOT NULL,
  category_id BIGINT NOT NULL,
  name VARCHAR(200) NOT NULL,
  description TEXT NULL,
  status VARCHAR(30) NOT NULL,
  base_price INT NOT NULL,
  currency CHAR(3) NOT NULL DEFAULT 'KRW',
  stock_quantity INT NOT NULL,
  popularity_score BIGINT NOT NULL DEFAULT 0,
  deleted_at DATETIME(6) NULL,
  created_at DATETIME(6) NOT NULL,
  updated_at DATETIME(6) NOT NULL,
  PRIMARY KEY (id),
  KEY idx_product_category (category_id),
  KEY idx_product_price (base_price),
  KEY idx_product_popularity (popularity_score),
  CONSTRAINT fk_product_brand FOREIGN KEY (brand_id) REFERENCES brand(id),
  CONSTRAINT fk_product_category FOREIGN KEY (category_id) REFERENCES category(id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS product_price_history (
  id BIGINT NOT NULL,
  product_id BIGINT NOT NULL,
  old_price INT NOT NULL,
  new_price INT NOT NULL,
  reason VARCHAR(200) NULL,
  created_at DATETIME(6) NOT NULL,
  PRIMARY KEY (id),
  KEY idx_pph_product (product_id),
  CONSTRAINT fk_pph_product FOREIGN KEY (product_id) REFERENCES product(id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS product_stock_history (
  id BIGINT NOT NULL,
  product_id BIGINT NOT NULL,
  old_stock INT NOT NULL,
  new_stock INT NOT NULL,
  reason VARCHAR(200) NULL,
  created_at DATETIME(6) NOT NULL,
  PRIMARY KEY (id),
  KEY idx_psh_product (product_id),
  CONSTRAINT fk_psh_product FOREIGN KEY (product_id) REFERENCES product(id)
) ENGINE=InnoDB;
