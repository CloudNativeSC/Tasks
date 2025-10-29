package cloudnative.spring.domain.task.dto.response.TimeAdjustment;


import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Getter
@Builder
public class TimeAdjustmentResponse {
    /**
     * 사용자 ID
     */
    private String userId;

    /**
     * 전체 평균 보정률 (예: 1.42 = 평균적으로 예상의 1.42배 소요)
     */
    private Double adjustmentRatio;

    /**
     * 카테고리별 보정률 (카테고리명 → 보정률)
     * 예: {"업무": 1.3, "공부": 1.5, "운동": 1.0}
     */
    private Map<String, Double> categoryRatios;

    /**
     * 사용자에게 보여줄 제안 메시지
     */
    private String suggestion;

    /**
     * 분석에 사용된 완료된 Task 개수
     */
    private Integer analyzedTaskCount;

    /**
     * 총 예상 시간 (분)
     */
    private Long totalEstimatedMinutes;

    /**
     * 총 실제 시간 (분)
     */
    private Long totalActualMinutes;
}
