package com.pjh.product.adapters.in.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pjh.product.application.port.in.ProductCommandUseCase;
import com.pjh.product.domain.model.ProductStatus;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.MariaDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class AdminProductControllerTest {

    private static final long BRAND_ID = 101L;
    private static final long CATEGORY_ID = 201L;

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
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private ProductCommandUseCase productCommandUseCase;

    @BeforeEach
    void setUp() {
        cleanupTables();
        insertBrand(BRAND_ID, "ControllerBrand");
        insertCategory(CATEGORY_ID, "ControllerCategory");
    }

    @Test
    void createProduct_shouldReturnEnvelope() throws Exception {
        var payload = CreateProductPayload.of(BRAND_ID, CATEGORY_ID);

        mockMvc.perform(post("/admin/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(payload)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.name").value("Controller Product"));
    }

    @Test
    void changePrice_shouldRecordHistory() throws Exception {
        Long productId = createSampleProduct();

        mockMvc.perform(patch("/admin/products/{id}/price", productId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"newPrice\":150000,\"reason\":\"AdminTest\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.newPrice").value(150000));

        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM product_price_history WHERE product_id = ?",
                Integer.class,
                productId
        );
        assertThat(count).isEqualTo(1);
    }

    @Test
    void deleteProduct_shouldSoftDelete() throws Exception {
        Long productId = createSampleProduct();

        mockMvc.perform(delete("/admin/products/{id}", productId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        Integer softDeleted = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM product WHERE id = ? AND deleted_at IS NOT NULL",
                Integer.class,
                productId
        );
        assertThat(softDeleted).isEqualTo(1);
    }

    private Long createSampleProduct() {
        var command = new ProductCommandUseCase.CreateProductCommand(
                null,
                333L,
                BRAND_ID,
                CATEGORY_ID,
                "Base Controller Product",
                "desc",
                ProductStatus.ACTIVE,
                120000,
                "KRW",
                10
        );
        return productCommandUseCase.createProduct(command).id();
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

    private void cleanupTables() {
        jdbcTemplate.execute("DELETE FROM product_price_history");
        jdbcTemplate.execute("DELETE FROM product_stock_history");
        jdbcTemplate.execute("DELETE FROM product");
        jdbcTemplate.execute("DELETE FROM category_closure");
        jdbcTemplate.execute("DELETE FROM category");
        jdbcTemplate.execute("DELETE FROM brand");
    }

    private static class CreateProductPayload {
        private Long sellerId;
        private Long brandId;
        private Long categoryId;
        private String name;
        private String description;
        private ProductStatus status;
        private int basePrice;
        private String currency;
        private int stockQuantity;

        static CreateProductPayload of(Long brandId, Long categoryId) {
            CreateProductPayload payload = new CreateProductPayload();
            payload.sellerId = 9900L;
            payload.brandId = brandId;
            payload.categoryId = categoryId;
            payload.name = "Controller Product";
            payload.description = "Controller description";
            payload.status = ProductStatus.ACTIVE;
            payload.basePrice = 130000;
            payload.currency = "KRW";
            payload.stockQuantity = 20;
            return payload;
        }

        public Long getSellerId() {
            return sellerId;
        }

        public Long getBrandId() {
            return brandId;
        }

        public Long getCategoryId() {
            return categoryId;
        }

        public String getName() {
            return name;
        }

        public String getDescription() {
            return description;
        }

        public ProductStatus getStatus() {
            return status;
        }

        public int getBasePrice() {
            return basePrice;
        }

        public String getCurrency() {
            return currency;
        }

        public int getStockQuantity() {
            return stockQuantity;
        }
    }
}
