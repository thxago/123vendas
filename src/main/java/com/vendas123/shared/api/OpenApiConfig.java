package com.vendas123.shared.api;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI salesOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("123Vendas - Sales API")
                        .description("CRUD de Vendas com regras de negócio e observabilidade")
                        .version("0.1.0"));
    }
}
