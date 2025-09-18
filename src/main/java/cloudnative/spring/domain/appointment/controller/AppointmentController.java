package cloudnative.spring.domain.appointment.controller;

import cloudnative.spring.domain.appointment.repository.AppointmentRepository;
import cloudnative.spring.domain.appointment.service.AppointmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/appointments") // 약속 api
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;
    //약속 장소 검색
    @GetMapping("/places/search")
    public ResponseEntity<?> searchPlaces(@RequestParam String place) {


    }

}
