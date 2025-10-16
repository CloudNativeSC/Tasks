package cloudnative.spring.external.exception;

import lombok.Getter;

/**
 * 외부 서비스 호출 시 발생하는 예외
 *
 * @author CloudNative Team
 * @since 2025-10-16
 */
@Getter
public class ExternalServiceException extends RuntimeException {

    private final String serviceName;
    private final int statusCode;
    private final String responseBody;

    public ExternalServiceException(String serviceName, String message, int statusCode, String responseBody) {
        super(String.format("[%s] %s (Status: %d)", serviceName, message, statusCode));
        this.serviceName = serviceName;
        this.statusCode = statusCode;
        this.responseBody = responseBody;
    }

    public ExternalServiceException(String serviceName, String message, int statusCode) {
        this(serviceName, message, statusCode, "");
    }
}