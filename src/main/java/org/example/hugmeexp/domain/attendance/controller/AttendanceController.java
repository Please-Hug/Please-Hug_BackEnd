package org.example.hugmeexp.domain.attendance.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.hugmeexp.domain.attendance.dto.AttendanceCheckRequest;
import org.example.hugmeexp.domain.attendance.dto.AttendanceCheckResponse;
import org.example.hugmeexp.domain.attendance.dto.AttendanceStatusResponse;
import org.example.hugmeexp.domain.attendance.service.AttendanceService;
import org.example.hugmeexp.global.common.response.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
     *
     * @param userId 사용자 ID
     * @return 출석 상태 응답
     */
    @GetMapping("/{userId}/status")
    public ResponseEntity<Response<AttendanceStatusResponse>> getAttendanceStatus(@PathVariable Long userId) {
        AttendanceStatusResponse data = attendanceService.getAttendanceStatus(userId);
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
     *
     * @param userId 사용자 ID
     * @param request 출석 체크 요청 데이터
     * @return 출석 체크 응답
     */
    @PostMapping("/{userId}/check")
    public ResponseEntity<Response<AttendanceCheckResponse>> checkAttendance(@PathVariable Long userId, @RequestBody AttendanceCheckRequest request) {
        AttendanceCheckResponse data = attendanceService.checkAttendance(userId, request);
        Response<AttendanceCheckResponse> response = Response.<AttendanceCheckResponse>builder()
                .data(data)
                .message("Attendance check success")
                .build();
        return ResponseEntity.ok(response);
    }
}
