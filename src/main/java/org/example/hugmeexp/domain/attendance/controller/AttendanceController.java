package org.example.hugmeexp.domain.attendance.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@Tag(name = "Attendance", description = "출석체크 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/attendance")
@Slf4j
public class AttendanceController {



    private final AttendanceService attendanceService;

    @Operation(summary = "출석 상태 조회", description = "일주일 출석 여부/연속출석/오늘 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
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


    @Operation(summary = "출석 체크하기", description = "오늘 출석 체크 (경험치와 구름조각 지급)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "출석체크 성공"),
            @ApiResponse(responseCode = "400", description = "이미 출석했거나 잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
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

    @Operation(
            summary = "출석 날짜 전체 조회",
            description = "로그인한 사용자가 출석한 모든 날짜를 yyyy-MM-dd 문자열 리스트로 반환"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
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
