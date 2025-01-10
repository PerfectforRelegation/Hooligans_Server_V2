package com.joh.core.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityScheme.In;
import io.swagger.v3.oas.models.security.SecurityScheme.Type;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {

        SecurityScheme securityScheme = new SecurityScheme()
            .name("Authorization")
            .type(Type.HTTP)
            .scheme("bearer")
            .bearerFormat("JWT")
            .in(In.HEADER);

        SecurityScheme userIdScheme = new SecurityScheme()
            .name("X-USER-ID")
            .type(Type.APIKEY) // API Key 타입
            .in(In.HEADER);    // 헤더로 전달

        return new OpenAPI()
            .components(new Components()
                .addSecuritySchemes("Authorization", securityScheme)
                .addSecuritySchemes("X-USER-ID", userIdScheme)
            )
            .addSecurityItem(new SecurityRequirement()
                .addList("Authorization") // 보안 적용
                .addList("X-USER-ID")
            )
            .addServersItem(new Server().url("/core"))
            .info(apiInfo());
    }

    private Info apiInfo() {
        return new Info()
            .title("API Test")          // API 제목
            .description("API 테스트 swagger")  // API에 대한 설명
            .version("1.0.0");
    }
}
