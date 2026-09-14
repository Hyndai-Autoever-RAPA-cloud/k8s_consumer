package com.hyundai.consumer.global.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI consumerOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Consumer API")
                        .description("도서 조회 Consumer 서비스 API 명세")
                        .version("v1"));
    }
}
