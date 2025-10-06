package cloudnative.spring.global.response.status;


import org.springframework.http.HttpStatus;

public enum ErrorCode {

    // 4xx Client Error
    BAD_REQUEST("400", "잘못된 요청입니다.", HttpStatus.BAD_REQUEST),
    UNAUTHORIZED("401", "인증이 필요합니다.", HttpStatus.UNAUTHORIZED),
    FORBIDDEN("403", "접근 권한이 없습니다.", HttpStatus.FORBIDDEN),
    NOT_FOUND("404", "요청한 리소스를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    METHOD_NOT_ALLOWED("405", "허용되지 않은 HTTP 메서드입니다.", HttpStatus.METHOD_NOT_ALLOWED),
    CONFLICT("409", "리소스 충돌이 발생했습니다.", HttpStatus.CONFLICT),

    // Validation Errors
    VALIDATION_ERROR("400", "유효성 검사에 실패했습니다.", HttpStatus.BAD_REQUEST),
    INVALID_PARAMETER("400", "잘못된 파라미터입니다.", HttpStatus.BAD_REQUEST),
    MISSING_REQUIRED_FIELD("400", "필수 필드가 누락되었습니다.", HttpStatus.BAD_REQUEST),

    // 5xx Server Error
    _INTERNAL_SERVER_ERROR("500", "서버 내부 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    SERVICE_UNAVAILABLE("503", "서비스를 사용할 수 없습니다.", HttpStatus.SERVICE_UNAVAILABLE),

    // Business Logic Errors
    USER_NOT_FOUND("404", "사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    DUPLICATE_USER("409", "이미 존재하는 사용자입니다.", HttpStatus.CONFLICT),
    INVALID_PASSWORD("400", "비밀번호가 올바르지 않습니다.", HttpStatus.BAD_REQUEST);

    private final String code;
    private final String message;
    private final HttpStatus httpStatus;

    ErrorCode(String code, String message, HttpStatus httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}