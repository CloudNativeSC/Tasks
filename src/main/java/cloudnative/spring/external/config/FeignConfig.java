package cloudnative.spring.external.config;

import cloudnative.spring.external.decoder.CustomErrorDecoder;
import feign.Logger;
import feign.Request;
import feign.RequestInterceptor;
import feign.Retryer;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Feign Client 전역 설정
 *
 * -모든 Feign Client에 공통으로 적용되는 설정
 *
 */
@Configuration
public class FeignConfig {

    /**
     * 로그 레벨 설정
     *
     * NONE: 로깅 안 함
     * BASIC: 요청 메소드, URL, 응답 상태 코드, 실행 시간
     * HEADERS: BASIC + 요청/응답 헤더
     * FULL: HEADERS + 요청/응답 본문
     */
    @Bean
    public Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;  // 개발: FULL, 운영: BASIC
    }

    /**
     * 요청 타임아웃 설정
     *
     * connectTimeout: 연결 대기 시간 (밀리초)
     * readTimeout: 응답 대기 시간 (밀리초)
     * followRedirects: 리다이렉트 따라가기 여부
     */
    @Bean
    public Request.Options feignOptions() {
        return new Request.Options(
                5000,   // connectTimeout (ms): 5초
                10000,  // readTimeout (ms): 10초
                true    // followRedirects: 리다이렉트 자동 처리
        );
    }

    /**
     * 재시도 설정
     *
     * period: 재시도 간격
     * maxPeriod: 최대 재시도 간격
     * maxAttempts: 최대 재시도 횟수
     */
    @Bean
    public Retryer feignRetryer() {
        return new Retryer.Default(
                100,   // 초기 재시도 간격 (ms)
                1000,  // 최대 재시도 간격 (ms)
                3      // 최대 재시도 횟수
        );
    }

    /**
     * 모든 요청에 공통 헤더를 추가합니다.
     */
    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            // User-Agent 헤더
            requestTemplate.header("User-Agent", "Task-Service/1.0");

            // Content-Type (POST/PUT/PATCH인 경우)
            if (isModifyingRequest(requestTemplate.method())) {
                requestTemplate.header("Content-Type", "application/json");
            }

            // Accept
            requestTemplate.header("Accept", "application/json");
        };
    }

    /**
     * Feign Client 호출 시 발생하는 에러를 커스텀 예외로 변환
     */
    @Bean
    public ErrorDecoder errorDecoder() {
        return new CustomErrorDecoder();
    }

    /**
     * 수정 요청인지 확인 (POST/PUT/PATCH/DELETE)
     */
    private boolean isModifyingRequest(String method) {
        return "POST".equals(method)
                || "PUT".equals(method)
                || "PATCH".equals(method)
                || "DELETE".equals(method);
    }
}