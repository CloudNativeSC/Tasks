package cloudnative.spring.domain.task.dto.response.Ai;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AiRecommendationResponse {

    @JsonProperty("user_id")
    private Integer userId;

    @JsonProperty("available_minutes")
    private Integer availableMinutes;

    @JsonProperty("fill_time")
    private Boolean fillTime;

    @JsonProperty("remaining_minutes")
    private Integer remainingMinutes;

    @JsonProperty("recommendations")
    private List<AiTaskRecommendation> recommendations;

    @JsonProperty("packed_recommendations")
    private List<AiTaskRecommendation> packedRecommendations;
}