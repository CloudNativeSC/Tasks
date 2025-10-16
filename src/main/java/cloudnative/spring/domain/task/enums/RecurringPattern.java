package cloudnative.spring.domain.task.enums;

/**
 * 반복 작업 패턴
 *
 * Task가 자동으로 반복 생성되는 주기를 정의합니다.
 *
 * @author CloudNative Team
 * @since 2025-10-16
 */
public enum RecurringPattern {

    /**
     * 매일 반복
     * 예: 아침 운동, 물 마시기, 일기 쓰기
     */
    DAILY,

    /**
     * 매주 반복 (원본 Task와 같은 요일)
     * 예: 매주 금요일 회의 (원본이 금요일이면 매주 금요일마다 생성)
     */
    WEEKLY,

    /**
     * 매월 반복 (원본 Task와 같은 일)
     * 예: 매월 1일 월급 관리, 매월 25일 카드 결제
     */
    MONTHLY
}