package cloudnative.spring.domain.task.client;

import cloudnative.spring.domain.task.dto.request.Ai.AiRecommendationRequest;
import cloudnative.spring.domain.task.dto.response.Ai.AiRecommendationResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
@RequiredArgsConstructor
public class AiRecommendationClient {

    private final RestTemplate restTemplate;

    @Value("${ai.recommendation.url}")
    private String aiServerUrl;

    public AiRecommendationResponse getRecommendations(AiRecommendationRequest request) {
        String url = aiServerUrl + "/recommendations";

        log.info("AI 추천 요청 - userId: {}, availableMinutes: {}, startHour: {}, weekday: {}",
                request.getUserId(),
                request.getAvailableMinutes(),
                request.getStartHour(),
                request.getWeekday());

        // HTTP 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<AiRecommendationRequest> entity = new HttpEntity<>(request, headers);

        try {
            AiRecommendationResponse response = restTemplate.postForObject(
                    url,
                    entity,
                    AiRecommendationResponse.class
            );

            if (response != null && response.getRecommendations() != null) {
                log.info("AI 추천 성공 - 추천 개수: {}, 남은 시간: {}분",
                        response.getRecommendations().size(),
                        response.getRemainingMinutes());
            } else {
                log.warn("AI 추천 응답이 비어있음");
            }

            return response;

        } catch (RestClientException e) {
            log.error("AI 추천 서버 통신 실패 - URL: {}, Error: {}", url, e.getMessage());
            throw new RuntimeException("AI 추천 서버와 통신할 수 없습니다.", e);
        }
    }
}