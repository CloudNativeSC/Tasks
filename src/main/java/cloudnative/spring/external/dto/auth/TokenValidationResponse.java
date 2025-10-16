package cloudnative.spring.external.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 토큰 검증 응답 DTO
 *
 * @author CloudNative Team
 * @since 2025-10-16
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenValidationResponse {

    /**
     * 토큰 유효 여부
     */
    private Boolean valid;

    /**
     * 사용자 ID (토큰에서 추출)
     */
    private String userId;

    /**
     * 사용자 이메일
     */
    private String email;

    /**
     * 토큰 만료 시간 (Unix timestamp)
     */
    private Long expiresAt;

    /**
     * 에러 메시지 (유효하지 않은 경우)
     */
    private String errorMessage;
}