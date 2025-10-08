package cloudnative.spring.domain.task.service;

import cloudnative.spring.domain.task.dto.request.CreateMicroHabitRequest;
import cloudnative.spring.domain.task.dto.response.MicroHabitCompletionResponse;
import cloudnative.spring.domain.task.dto.response.MicroHabitResponse;

import java.util.List;

public interface MicroHabitService {
    // 습관 생성
    MicroHabitResponse createMicroHabit(String userId, CreateMicroHabitRequest request);

    // 습관 목록 조회
    List<MicroHabitResponse> getMicroHabits(String userId);

    // 오늘 추천 습관 (랜덤)
    MicroHabitResponse getTodayRecommendedHabit(String userId);

    // 습관 완료 처리 (응원 메시지 포함)
    MicroHabitCompletionResponse completeMicroHabit(Long habitId, String userId);

    // 습관 삭제
    void deleteMicroHabit(Long habitId, String userId);
}