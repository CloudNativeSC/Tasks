package cloudnative.spring.domain.task.service.impl;

import cloudnative.spring.domain.task.dto.request.CreateMicroHabitRequest;
import cloudnative.spring.domain.task.dto.response.MicroHabitCompletionResponse;
import cloudnative.spring.domain.task.dto.response.MicroHabitResponse;
import cloudnative.spring.domain.task.entity.MicroHabit;
import cloudnative.spring.domain.task.enums.HabitType;
import cloudnative.spring.domain.task.enums.MessageType;
import cloudnative.spring.domain.task.repository.EncouragementMessageRepository;
import cloudnative.spring.domain.task.repository.MicroHabitRepository;
import cloudnative.spring.domain.task.service.MicroHabitService;
import cloudnative.spring.global.exception.handler.GeneralHandler;
import cloudnative.spring.global.response.status.ErrorCode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MicroHabitServiceImpl implements MicroHabitService {

    private final MicroHabitRepository microHabitRepository;
    private final EncouragementMessageRepository encouragementMessageRepository;
    private final Random random = new Random();

    @Override
    @Transactional
    public MicroHabitResponse createMicroHabit(String userId, CreateMicroHabitRequest request) {
        log.info("작은 습관 생성 - userId: {}, habitName: {}", userId, request.getHabitName());

        MicroHabit habit = MicroHabit.builder()
                .userId(userId)
                .habitName(request.getHabitName())
                .durationMinutes(request.getDurationMinutes())
                .habitType(request.getHabitType())
                .completionCount(0)
                .build();

        MicroHabit savedHabit = microHabitRepository.save(habit);
        log.info("작은 습관 생성 완료 - habitId: {}", savedHabit.getId());
        return MicroHabitResponse.from(savedHabit);
    }

    @Override
    public List<MicroHabitResponse> getMicroHabits(String userId) {
        log.debug("습관 목록 조회 - userId: {}", userId);
        List<MicroHabit> habits = microHabitRepository.findByUserIdOrderByCompletionCountDesc(userId);
        return habits.stream()
                .map(MicroHabitResponse::from)
                .collect(Collectors.toList());
    }

    @Override
    public MicroHabitResponse getTodayRecommendedHabit(String userId) {
        log.debug("오늘 추천 습관 조회 - userId: {}", userId);

        // 미리 정의된 습관 중 랜덤 선택
        List<MicroHabit> predefinedHabits = microHabitRepository.findByUserIdAndHabitType(
                userId, HabitType.PREDEFINED);

        if (predefinedHabits.isEmpty()) {
            throw new GeneralHandler(ErrorCode.NOT_FOUND);
        }

        MicroHabit randomHabit = predefinedHabits.get(random.nextInt(predefinedHabits.size()));
        return MicroHabitResponse.from(randomHabit);
    }

    @Override
    @Transactional
    public MicroHabitCompletionResponse completeMicroHabit(Long habitId, String userId) {
        log.info("습관 완료 처리 - habitId: {}, userId: {}", habitId, userId);

        MicroHabit habit = microHabitRepository.findById(habitId)
                .orElseThrow(() -> new GeneralHandler(ErrorCode.NOT_FOUND));

        // 권한 확인
        if (!habit.getUserId().equals(userId)) {
            throw new GeneralHandler(ErrorCode.FORBIDDEN);
        }

        // 완료 횟수 증가
        habit.incrementCompletion();
        microHabitRepository.save(habit);

        // 응원 메시지 선택
        String encouragementMessage = selectEncouragementMessage(habit.getCompletionCount());

        log.info("습관 완료 - habitId: {}, 완료 횟수: {}", habitId, habit.getCompletionCount());

        return MicroHabitCompletionResponse.builder()
                .habit(MicroHabitResponse.from(habit))
                .encouragementMessage(encouragementMessage)
                .completionCount(habit.getCompletionCount())
                .build();
    }

    @Override
    @Transactional
    public void deleteMicroHabit(Long habitId, String userId) {
        log.info("습관 삭제 - habitId: {}, userId: {}", habitId, userId);

        MicroHabit habit = microHabitRepository.findById(habitId)
                .orElseThrow(() -> new GeneralHandler(ErrorCode.NOT_FOUND));

        if (!habit.getUserId().equals(userId)) {
            throw new GeneralHandler(ErrorCode.FORBIDDEN);
        }

        microHabitRepository.delete(habit);
    }

    // 응원 메시지 선택 로직
    private String selectEncouragementMessage(int count) {
        MessageType messageType;

        if (count == 1) {
            messageType = MessageType.INITIAL;
        } else if (count == 10 || count == 20 || count == 50 || count == 100) {
            messageType = MessageType.MILESTONE;
        } else if (count % 30 == 0) {
            messageType = MessageType.CONSISTENT;
        } else {
            messageType = MessageType.GENERAL;
        }

        // DB에서 랜덤 메시지 조회
        String message = encouragementMessageRepository.findRandomMessageByType(messageType);

        if (message == null) {
            return "잘하고 있어요! 계속 응원할게요! 🎉";  // 기본 메시지
        }

        return message;
    }
}