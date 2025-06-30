package org.example.hugmeexp.domain.attendance.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.hugmeexp.domain.attendance.dto.AttendanceCheckResponse;
import org.example.hugmeexp.domain.attendance.dto.AttendanceStatusResponse;
import org.example.hugmeexp.domain.attendance.service.AttendanceService;
import org.example.hugmeexp.global.common.response.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/attendance")
@Slf4j
public class AttendanceController {

    private final AttendanceService attendanceService;

    /**
     * 출석 상태 조회
     * - 사용자 ID를 통해 해당 사용자의 출석 상태를 조회
     * - 연속 출석 일수와 오늘 날짜도 함께 반환
     */
    @GetMapping("/status")
    public ResponseEntity<Response<AttendanceStatusResponse>> getAttendanceStatus(
            @AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        AttendanceStatusResponse data = attendanceService.getAttendanceStatus(username);
        if (data == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Response.<AttendanceStatusResponse>builder()
                            .data(null)
                            .message("Attendance status is null")
                            .build());
        }
        Response<AttendanceStatusResponse> response = Response.<AttendanceStatusResponse>builder()
                .data(data)
                .message("Attendance status retrieved successfully")
                .build();
        return ResponseEntity.ok(response);
    }


    /**
     * 출석 체크
     * - 사용자 ID와 출석 체크 요청 데이터를 통해 출석 체크를 수행
     * - 성공 여부, 연속 출석 일수, 오늘의 출석 상태 등을 반환
     */
    @PostMapping("/check")
    public ResponseEntity<Response<AttendanceCheckResponse>> checkAttendance(
            @AuthenticationPrincipal UserDetails userDetails){
        String username = userDetails.getUsername();
        AttendanceCheckResponse data = attendanceService.checkAttendance(username);
        Response<AttendanceCheckResponse> response = Response.<AttendanceCheckResponse>builder()
                .data(data)
                .message("Attendance check success")
                .build();
        return ResponseEntity.ok(response);
    }

    /**
     * 한 유저가 출석한 전체 날짜 조회
     * 출석한 날짜들을 리스트로 반환
     */
    @GetMapping("/dates")
    public ResponseEntity<Response<List<String>>> getAllDates(
            @AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        List<LocalDate> dates = attendanceService.getAllAttendanceDates(username);
        List<String> dateStrings = dates.stream()
                .map(LocalDate::toString)
                .toList();
        return ResponseEntity.ok(
                Response.<List<String>>builder()
                        .data(dateStrings)
                        .message("all Attendance dates retrieved success")
                        .build()
        );
    }
}
