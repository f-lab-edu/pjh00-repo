package com.pjh.product.application.service;

import com.pjh.product.application.port.in.ProductCommandUseCase;
import com.pjh.product.domain.model.ProductStatus;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MariaDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
class ProductCommandServiceTest {

    private static final long BRAND_ID = 100L;
    private static final long CATEGORY_ID = 200L;

    @Container
    private static final MariaDBContainer<?> mariaDB = new MariaDBContainer<>("mariadb:11.3")
            .withDatabaseName("product_db")
            .withUsername("product")
            .withPassword("product")
            .withInitScript("db/init.sql");

    @DynamicPropertySource
    static void overrideProps(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mariaDB::getJdbcUrl);
        registry.add("spring.datasource.username", mariaDB::getUsername);
        registry.add("spring.datasource.password", mariaDB::getPassword);
        registry.add("spring.datasource.driver-class-name", mariaDB::getDriverClassName);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "none");
    }

    @Autowired
    private ProductCommandUseCase productCommandUseCase;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        cleanupTables();
        insertBrand(BRAND_ID, "TestBrand");
        insertCategory(CATEGORY_ID, "CategoryRoot");
    }

    @Test
    void shouldCreateProduct() {
        var command = new ProductCommandUseCase.CreateProductCommand(
                null,
                999L,
                BRAND_ID,
                CATEGORY_ID,
                "Winter Jacket",
                "Warm jacket",
                ProductStatus.ACTIVE,
                120000,
                "KRW",
                10
        );

        var result = productCommandUseCase.createProduct(command);

        assertThat(result.id()).isNotNull();
        assertThat(result.name()).isEqualTo("Winter Jacket");

        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM product WHERE id = ? AND deleted_at IS NULL",
                Integer.class,
                result.id()
        );
        assertThat(count).isEqualTo(1);
    }

    @Test
    void shouldChangePriceAndRecordHistory() {
        Long productId = createSampleProduct();

        var result = productCommandUseCase.changePrice(productId, new ProductCommandUseCase.ChangePriceCommand(135000, "Promo"));

        assertThat(result.oldPrice()).isEqualTo(110000);
        assertThat(result.newPrice()).isEqualTo(135000);

        Integer historyCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM product_price_history WHERE product_id = ?",
                Integer.class,
                productId
        );
        assertThat(historyCount).isEqualTo(1);
    }

    @Test
    void shouldChangeStockAndRecordHistory() {
        Long productId = createSampleProduct();

        var result = productCommandUseCase.changeStock(productId, new ProductCommandUseCase.ChangeStockCommand(40, "Restock"));

        assertThat(result.oldStock()).isEqualTo(25);
        assertThat(result.newStock()).isEqualTo(40);

        Integer historyCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM product_stock_history WHERE product_id = ?",
                Integer.class,
                productId
        );
        assertThat(historyCount).isEqualTo(1);
    }

    @Test
    void shouldSoftDeleteProduct() {
        Long productId = createSampleProduct();

        productCommandUseCase.deleteProduct(productId);

        Timestamp deletedAt = jdbcTemplate.queryForObject(
                "SELECT deleted_at FROM product WHERE id = ?",
                Timestamp.class,
                productId
        );
        assertThat(deletedAt).isNotNull();
    }

    private Long createSampleProduct() {
        var command = new ProductCommandUseCase.CreateProductCommand(
                null,
                500L,
                BRAND_ID,
                CATEGORY_ID,
                "Sample Product",
                "Sample description",
                ProductStatus.ACTIVE,
                110000,
                "KRW",
                25
        );
        return productCommandUseCase.createProduct(command).id();
    }

    private void cleanupTables() {
        jdbcTemplate.execute("DELETE FROM product_price_history");
        jdbcTemplate.execute("DELETE FROM product_stock_history");
        jdbcTemplate.execute("DELETE FROM product");
        jdbcTemplate.execute("DELETE FROM category_closure");
        jdbcTemplate.execute("DELETE FROM category");
        jdbcTemplate.execute("DELETE FROM brand");
    }

    private void insertBrand(long id, String name) {
        jdbcTemplate.update(
                "INSERT INTO brand(id, name, deleted_at, created_at, updated_at) VALUES (?, ?, NULL, ?, ?)",
                id,
                name,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    private void insertCategory(long id, String name) {
        jdbcTemplate.update(
                "INSERT INTO category(id, parent_id, name, depth, is_active, deleted_at, created_at, updated_at) VALUES (?, NULL, ?, 0, 1, NULL, ?, ?)",
                id,
                name,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }
}
