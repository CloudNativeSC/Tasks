package cloudnative.spring.external.dto.appointment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Appointment 응답 DTO
 *
 * @author CloudNative Team
 * @since 2025-10-16
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentResponse {

    /**
     * 일정 ID
     */
    private String id;

    /**
     * 사용자 ID
     */
    private String userId;

    /**
     * 일정 제목
     */
    private String title;

    /**
     * 일정 설명
     */
    private String description;

    /**
     * 일정 날짜
     */
    private LocalDate appointmentDate;

    /**
     * 시작 시간
     */
    private LocalTime startTime;

    /**
     * 종료 시간
     */
    private LocalTime endTime;

    /**
     * 위치
     */
    private String location;

    /**
     * 알림 설정 (분 단위)
     */
    private Integer reminderMinutes;

    /**
     * 생성일시
     */
    private String createdAt;

    /**
     * 수정일시
     */
    private String updatedAt;
}