package com.remitly.stockmarket.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

        @Bean
        public OpenAPI customOpenAPI() {
                return new OpenAPI()
                                .info(new Info()
                                                .title("Stock Market Simulator API")
                                                .version("1.0")
                                                .description("Simplified stock exchange simulator for Remitly coding task")
                                                .contact(new Contact()
                                                                .name("Michał Matsalak")
                                                                .email("matsalakmichal@gmail.com"))
                                                .license(new License()
                                                                .name("MIT")
                                                                .url("https://opensource.org/licenses/MIT")));
        }
}