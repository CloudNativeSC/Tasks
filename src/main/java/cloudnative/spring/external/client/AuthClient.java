package cloudnative.spring.external.client;

import cloudnative.spring.external.config.FeignConfig;
import cloudnative.spring.external.dto.auth.TokenValidationResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;

/**
 * Auth Service Feign Client
 *
 * 인증/인가 서비스와 통신하는 Feign Client
 *
 * @author CloudNative Team
 * @since 2025-10-16
 */
@FeignClient(
        name = "auth-service",
        url = "${external.services.auth.url}",
        configuration = FeignConfig.class
)
public interface AuthClient {

    /**
     * 액세스 토큰 검증
     *
     * @param authorization Bearer 토큰 (예: "Bearer eyJhbGc...")
     * @return 토큰 검증 결과
     */
    @PostMapping("/api/auth/validate")
    TokenValidationResponse validateToken(
            @RequestHeader("Authorization") String authorization
    );

    /**
     * 토큰에서 사용자 ID 추출
     *
     * @param authorization Bearer 토큰
     * @return 사용자 ID
     */
    @GetMapping("/api/auth/user-id")
    String extractUserId(
            @RequestHeader("Authorization") String authorization
    );

    /**
     * 토큰 갱신 (리프레시 토큰 사용)
     *
     * @param refreshToken 리프레시 토큰
     * @return 새로운 액세스 토큰
     */
    @PostMapping("/api/auth/refresh")
    String refreshToken(
            @RequestHeader("Refresh-Token") String refreshToken
    );
}