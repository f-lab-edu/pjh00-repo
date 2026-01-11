package com.pjh.product.infrastructure.config;

import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public GroupedOpenApi adminApi() {
        return GroupedOpenApi.builder()
                .group("admin")
                .addOpenApiCustomizer(openApi -> openApi.info(new Info()
                        .title("Product Admin API")
                        .description("Administration endpoints")))
                .pathsToMatch("/admin/**")
                .build();
    }

    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("public")
                .addOpenApiCustomizer(openApi -> openApi.info(new Info()
                        .title("Product Public API")
                        .description("Public endpoints")))
                .pathsToMatch("/api/public/**")
                .build();
    }
}
