package cloudnative.spring.global.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("dev")  //dev 환경에서만 Swagger 등록
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Cluvy Task Service API")
                        .description("Cluvy 프로젝트 Task 서비스 문서 (개발 환경 전용)")
                        .version("v1.0.0"));
    }
}
