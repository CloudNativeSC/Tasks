package cloudnative.spring.domain.task.dto.request.Ai;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AiRecommendationRequest {

    @JsonProperty("user_id")
    private Integer userId;

    @JsonProperty("available_minutes")
    private Integer availableMinutes;

    @JsonProperty("num_recommendations")
    @Builder.Default
    private Integer numRecommendations = 5;

    @JsonProperty("start_hour")
    private Integer startHour;

    @JsonProperty("weekday")
    private Integer weekday;

    @JsonProperty("fill_time")
    @Builder.Default
    private Boolean fillTime = false;
}