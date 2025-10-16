package cloudnative.spring.external.client;

import cloudnative.spring.external.config.FeignConfig;
import cloudnative.spring.external.dto.appointment.AppointmentResponse;
import cloudnative.spring.external.dto.appointment.CreateAppointmentRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Appointment Service Feign Client
 *
 * 일정 관리 서비스와 통신하는 Feign Client
 *
 * @author CloudNative Team
 * @since 2025-10-16
 */
@FeignClient(
        name = "appointment-service",
        url = "${external.services.appointment.url}",
        configuration = FeignConfig.class
)
public interface AppointmentClient {

    /**
     * 특정 날짜의 일정 조회
     *
     * @param userId 사용자 ID
     * @param date 조회 날짜
     * @return 일정 목록
     */
    @GetMapping("/api/appointments")
    List<AppointmentResponse> getAppointmentsByDate(
            @RequestParam("userId") String userId,
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    );

    /**
     * 일정 생성
     *
     * @param userId 사용자 ID
     * @param request 일정 생성 요청
     * @return 생성된 일정
     */
    @PostMapping("/api/appointments")
    AppointmentResponse createAppointment(
            @RequestParam("userId") String userId,
            @RequestBody CreateAppointmentRequest request
    );

    /**
     * 일정 상세 조회
     *
     * @param appointmentId 일정 ID
     * @return 일정 상세 정보
     */
    @GetMapping("/api/appointments/{appointmentId}")
    AppointmentResponse getAppointmentById(
            @PathVariable("appointmentId") String appointmentId
    );

    /**
     * 일정 삭제
     *
     * @param appointmentId 일정 ID
     */
    @DeleteMapping("/api/appointments/{appointmentId}")
    void deleteAppointment(
            @PathVariable("appointmentId") String appointmentId
    );

    /**
     * 사용자의 오늘 일정 개수 조회
     *
     * @param userId 사용자 ID
     * @return 오늘 일정 개수
     */
    @GetMapping("/api/appointments/count/today")
    Integer getTodayAppointmentCount(
            @RequestParam("userId") String userId
    );
}