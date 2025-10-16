package cloudnative.spring.external.client;

import cloudnative.spring.external.config.FeignConfig;
import cloudnative.spring.external.dto.user.UserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

/**
 * User Service Feign Client
 *
 * User 서비스와 통신하는 Feign Client 인터페이스
 *
 * <p>설정:
 * <ul>
 *   <li>name: Feign Client 이름</li>
 *   <li>url: User 서비스 URL (application.yml에서 주입)</li>
 *   <li>configuration: 공통 설정 클래스</li>
 * </ul>
 *
 * @author CloudNative Team
 * @since 2025-10-16
 */
@FeignClient(
        name = "user-service",
        url = "${external.services.user.url}",
        configuration = FeignConfig.class
)
public interface UserClient {

    /**
     * 사용자 정보 조회
     *
     * @param userId 사용자 ID
     * @return 사용자 정보
     */
    @GetMapping("/api/users/{userId}")
    UserResponse getUserById(@PathVariable("userId") String userId);

    /**
     * 사용자 정보 조회 (인증 포함)
     *
     * Gateway를 통해 호출 시 Authorization 헤더가 필요한 경우
     *
     * @param userId 사용자 ID
     * @param authorization Bearer 토큰 (예: "Bearer eyJhbGc...")
     * @return 사용자 정보
     */
    @GetMapping("/api/users/{userId}")
    UserResponse getUserByIdWithAuth(
            @PathVariable("userId") String userId,
            @RequestHeader("Authorization") String authorization
    );

    /**
     * 사용자 존재 여부 확인
     *
     * @param userId 사용자 ID
     * @return 존재 여부 (true/false)
     */
    @GetMapping("/api/users/{userId}/exists")
    Boolean checkUserExists(@PathVariable("userId") String userId);
}