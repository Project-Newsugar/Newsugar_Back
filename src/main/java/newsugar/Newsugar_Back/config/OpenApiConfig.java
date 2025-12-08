package newsugar.Newsugar_Back.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    // 스웨거(OpenAPI) 기본 정보 및 JWT 보안 스키마 설정
    @Bean
    public OpenAPI openAPI() {
        SecurityScheme bearer = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT");

        return new OpenAPI()
                .info(new Info()
                        .title("Newsugar API")
                        .version("v1")
                        .description("뉴스 요약 서비스 API 스펙"))
                .components(new Components().addSecuritySchemes("bearerAuth", bearer))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
    }
}

