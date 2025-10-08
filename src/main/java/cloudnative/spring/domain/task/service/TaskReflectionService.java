package cloudnative.spring.domain.task.service;

import cloudnative.spring.domain.task.dto.request.CreateReflectionRequest;
import cloudnative.spring.domain.task.dto.request.UpdateReflectionRequest;
import cloudnative.spring.domain.task.dto.response.TaskReflectionResponse;

import java.util.List;

public interface TaskReflectionService {
    // 워밍업: 작업 시작 전 회고 생성
    TaskReflectionResponse createWarmup(String userId, CreateReflectionRequest request);

    // 쿨다운: 작업 완료 후 회고 추가
    TaskReflectionResponse addCooldown(Long reflectionId, UpdateReflectionRequest request);

    // 회고 목록 조회
    List<TaskReflectionResponse> getReflections(String userId);

    // 카테고리별 회고 조회
    List<TaskReflectionResponse> getReflectionsByCategory(String userId, String categoryName);

    // 특정 회고 조회
    TaskReflectionResponse getReflectionById(Long reflectionId);

    // 미완성 회고 조회 (쿨다운 미작성)
    List<TaskReflectionResponse> getIncompleteReflections(String userId);

    // 회고 삭제
    void deleteReflection(Long reflectionId, String userId);
}