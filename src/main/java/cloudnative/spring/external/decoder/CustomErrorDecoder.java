package cloudnative.spring.external.decoder;

import cloudnative.spring.external.exception.ExternalServiceException;
import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * Feign Client 에러 처리 커스텀 디코더
 *
 * 외부 서비스 호출 시 발생하는 HTTP 에러를
 * 애플리케이션 예외로 변환합니다.
 *
 * @author CloudNative Team
 * @since 2025-10-16
 */
@Slf4j
public class CustomErrorDecoder implements ErrorDecoder {

    private final ErrorDecoder defaultErrorDecoder = new Default();

    @Override
    public Exception decode(String methodKey, Response response) {
        String serviceName = extractServiceName(methodKey);
        int status = response.status();
        String responseBody = extractResponseBody(response);

        log.error("External service error - service: {}, method: {}, status: {}, body: {}",
                serviceName, methodKey, status, responseBody);

        switch (status) {
            case 400:
                return new ExternalServiceException(
                        serviceName,
                        "잘못된 요청입니다. 요청 파라미터를 확인하세요.",
                        status,
                        responseBody
                );

            case 401:
                return new ExternalServiceException(
                        serviceName,
                        "인증에 실패했습니다. 토큰을 확인하세요.",
                        status,
                        responseBody
                );

            case 403:
                return new ExternalServiceException(
                        serviceName,
                        "권한이 없습니다.",
                        status,
                        responseBody
                );

            case 404:
                return new ExternalServiceException(
                        serviceName,
                        "요청한 리소스를 찾을 수 없습니다.",
                        status,
                        responseBody
                );

            case 500:
                return new ExternalServiceException(
                        serviceName,
                        "외부 서비스에서 오류가 발생했습니다.",
                        status,
                        responseBody
                );

            case 503:
                return new ExternalServiceException(
                        serviceName,
                        "외부 서비스를 사용할 수 없습니다. 잠시 후 다시 시도하세요.",
                        status,
                        responseBody
                );

            default:
                return defaultErrorDecoder.decode(methodKey, response);
        }
    }

    /**
     * methodKey에서 서비스명 추출
     *
     * 예: UserClient#getUserById(String) → UserClient
     */
    private String extractServiceName(String methodKey) {
        if (methodKey.contains("#")) {
            return methodKey.substring(0, methodKey.indexOf("#"));
        }
        return "Unknown";
    }

    /**
     * Response Body 추출
     */
    private String extractResponseBody(Response response) {
        try {
            if (response.body() != null) {
                InputStream inputStream = response.body().asInputStream();
                return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            }
        } catch (IOException e) {
            log.error("Failed to read response body", e);
        }
        return "";
    }
}