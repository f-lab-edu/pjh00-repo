/* =========================================================
  Database bootstrap (schemas + service accounts only)
========================================================= */
CREATE DATABASE IF NOT EXISTS seller_db       CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS product_db      CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS inventory_db    CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS cart_db         CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS coupon_db       CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS pricing_db      CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS order_db        CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS payment_db      CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS delivery_db     CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS review_db       CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS qna_db          CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS display_db      CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS notification_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS iam_db          CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

CREATE USER IF NOT EXISTS 'seller'@'%'        IDENTIFIED BY 'pjh0115';
CREATE USER IF NOT EXISTS 'product'@'%'       IDENTIFIED BY 'pjh0115';
CREATE USER IF NOT EXISTS 'inventory'@'%'     IDENTIFIED BY 'pjh0115';
CREATE USER IF NOT EXISTS 'cart'@'%'          IDENTIFIED BY 'pjh0115';
CREATE USER IF NOT EXISTS 'coupon'@'%'        IDENTIFIED BY 'pjh0115';
CREATE USER IF NOT EXISTS 'pricing'@'%'       IDENTIFIED BY 'pjh0115';
CREATE USER IF NOT EXISTS 'order'@'%'         IDENTIFIED BY 'pjh0115';
CREATE USER IF NOT EXISTS 'payment'@'%'       IDENTIFIED BY 'pjh0115';
CREATE USER IF NOT EXISTS 'delivery'@'%'      IDENTIFIED BY 'pjh0115';
CREATE USER IF NOT EXISTS 'review'@'%'        IDENTIFIED BY 'pjh0115';
CREATE USER IF NOT EXISTS 'qna'@'%'           IDENTIFIED BY 'pjh0115';
CREATE USER IF NOT EXISTS 'display'@'%'       IDENTIFIED BY 'pjh0115';
CREATE USER IF NOT EXISTS 'notification'@'%'  IDENTIFIED BY 'pjh0115';
CREATE USER IF NOT EXISTS 'iam'@'%'           IDENTIFIED BY 'pjh0115';

GRANT ALL PRIVILEGES ON seller_db.*        TO 'seller'@'%';
GRANT ALL PRIVILEGES ON product_db.*       TO 'product'@'%';
GRANT ALL PRIVILEGES ON inventory_db.*     TO 'inventory'@'%';
GRANT ALL PRIVILEGES ON cart_db.*          TO 'cart'@'%';
GRANT ALL PRIVILEGES ON coupon_db.*        TO 'coupon'@'%';
GRANT ALL PRIVILEGES ON pricing_db.*       TO 'pricing'@'%';
GRANT ALL PRIVILEGES ON order_db.*         TO 'order'@'%';
GRANT ALL PRIVILEGES ON payment_db.*       TO 'payment'@'%';
GRANT ALL PRIVILEGES ON delivery_db.*      TO 'delivery'@'%';
GRANT ALL PRIVILEGES ON review_db.*        TO 'review'@'%';
GRANT ALL PRIVILEGES ON qna_db.*           TO 'qna'@'%';
GRANT ALL PRIVILEGES ON display_db.*       TO 'display'@'%';
GRANT ALL PRIVILEGES ON notification_db.*  TO 'notification'@'%';
GRANT ALL PRIVILEGES ON iam_db.*           TO 'iam'@'%';

FLUSH PRIVILEGES;
