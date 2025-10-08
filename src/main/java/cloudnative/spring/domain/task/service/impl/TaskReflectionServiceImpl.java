package cloudnative.spring.domain.task.service.impl;


import cloudnative.spring.domain.task.dto.request.CreateReflectionRequest;
import cloudnative.spring.domain.task.dto.request.UpdateReflectionRequest;
import cloudnative.spring.domain.task.dto.response.TaskReflectionResponse;
import cloudnative.spring.domain.task.entity.Task;
import cloudnative.spring.domain.task.entity.TaskReflection;
import cloudnative.spring.domain.task.repository.TaskReflectionRepository;
import cloudnative.spring.domain.task.repository.TaskRepository;
import cloudnative.spring.domain.task.service.TaskReflectionService;
import cloudnative.spring.global.exception.handler.GeneralHandler;
import cloudnative.spring.global.response.status.ErrorCode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TaskReflectionServiceImpl implements TaskReflectionService {

    private final TaskReflectionRepository taskReflectionRepository;
    private final TaskRepository taskRepository;

    @Override
    @Transactional
    public TaskReflectionResponse createWarmup(String userId, CreateReflectionRequest request) {
        log.info("워밍업 회고 생성 - userId: {}, taskId: {}", userId, request.getTaskId());

        // Task 조회 및 카테고리 정보 가져오기
        Task task = taskRepository.findById(request.getTaskId())
                .orElseThrow(() -> new GeneralHandler(ErrorCode.TASK_NOT_FOUND));

        TaskReflection reflection = TaskReflection.builder()
                .userId(userId)
                .taskId(request.getTaskId())
                .workSessionId(request.getWorkSessionId())
                .warmupNote(request.getWarmupNote())
                .categoryName(task.getCategory() != null ? task.getCategory().getName() : null)
                .build();

        TaskReflection savedReflection = taskReflectionRepository.save(reflection);
        log.info("워밍업 회고 생성 완료 - reflectionId: {}", savedReflection.getId());
        return TaskReflectionResponse.from(savedReflection);
    }

    @Override
    @Transactional
    public TaskReflectionResponse addCooldown(Long reflectionId, UpdateReflectionRequest request) {
        log.info("쿨다운 회고 추가 - reflectionId: {}", reflectionId);

        TaskReflection reflection = taskReflectionRepository.findById(reflectionId)
                .orElseThrow(() -> new GeneralHandler(ErrorCode.NOT_FOUND));

        reflection.setCooldownNote(request.getCooldownNote());

        TaskReflection updatedReflection = taskReflectionRepository.save(reflection);
        log.info("쿨다운 회고 추가 완료 - reflectionId: {}", reflectionId);
        return TaskReflectionResponse.from(updatedReflection);
    }

    @Override
    public List<TaskReflectionResponse> getReflections(String userId) {
        log.debug("회고 목록 조회 - userId: {}", userId);
        List<TaskReflection> reflections = taskReflectionRepository.findByUserIdOrderByCreatedAtDesc(userId);
        return reflections.stream()
                .map(TaskReflectionResponse::from)
                .collect(Collectors.toList());
    }

    @Override
    public List<TaskReflectionResponse> getReflectionsByCategory(String userId, String categoryName) {
        log.debug("카테고리별 회고 조회 - userId: {}, category: {}", userId, categoryName);
        List<TaskReflection> reflections = taskReflectionRepository
                .findByUserIdAndCategoryNameOrderByCreatedAtDesc(userId, categoryName);
        return reflections.stream()
                .map(TaskReflectionResponse::from)
                .collect(Collectors.toList());
    }

    @Override
    public TaskReflectionResponse getReflectionById(Long reflectionId) {
        log.debug("회고 상세 조회 - reflectionId: {}", reflectionId);
        TaskReflection reflection = taskReflectionRepository.findById(reflectionId)
                .orElseThrow(() -> new GeneralHandler(ErrorCode.NOT_FOUND));
        return TaskReflectionResponse.from(reflection);
    }

    @Override
    public List<TaskReflectionResponse> getIncompleteReflections(String userId) {
        log.debug("미완성 회고 조회 - userId: {}", userId);
        List<TaskReflection> reflections = taskReflectionRepository.findByUserIdAndCooldownNoteIsNull(userId);
        return reflections.stream()
                .map(TaskReflectionResponse::from)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteReflection(Long reflectionId, String userId) {
        log.info("회고 삭제 - reflectionId: {}, userId: {}", reflectionId, userId);

        TaskReflection reflection = taskReflectionRepository.findById(reflectionId)
                .orElseThrow(() -> new GeneralHandler(ErrorCode.NOT_FOUND));

        // 권한 확인
        if (!reflection.getUserId().equals(userId)) {
            throw new GeneralHandler(ErrorCode.FORBIDDEN);
        }

        taskReflectionRepository.delete(reflection);
    }
}