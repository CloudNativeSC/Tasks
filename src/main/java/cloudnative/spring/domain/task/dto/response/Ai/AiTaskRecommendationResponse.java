package cloudnative.spring.domain.task.dto.response.Ai;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class AiTaskRecommendationResponse {

    @JsonProperty("user_id")
    private Integer userId;

    @JsonProperty("available_minutes")
    private Integer availableMinutes;

    @JsonProperty("remaining_minutes")
    private Integer remainingMinutes;


    @JsonProperty("fill_time")
    private Boolean fillTime;

    @JsonProperty("recommendations")
    private List<RecommendedTask> recommendations;

    @JsonProperty("packed_recommendations")
    private List<RecommendedTask> packedRecommendations;

    @Getter
    @Builder
    public static class RecommendedTask {

        @JsonProperty("task_name")
        private String taskName;

        @JsonProperty("task_category")
        private String taskCategory;

        @JsonProperty("task_duration")
        private Integer taskDuration;

        @JsonProperty("score")
        private Double score;

        @JsonProperty("tags")
        private List<String> tags;
    }
}