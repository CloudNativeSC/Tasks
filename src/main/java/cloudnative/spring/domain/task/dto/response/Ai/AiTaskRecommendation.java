package cloudnative.spring.domain.task.dto.response.Ai;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AiTaskRecommendation {

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