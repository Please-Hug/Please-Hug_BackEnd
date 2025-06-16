package org.example.hugmeexp.domain.attendance.service;

import org.example.hugmeexp.domain.attendance.dto.AttendanceCheckRequest;
import org.example.hugmeexp.domain.attendance.dto.AttendanceStatusResponse;
import org.springframework.stereotype.Service;

@Service
public class AttendacneService {

    public AttendanceStatusResponse getAttendanceStatus(boolean[] attendanceStatus, int continuounsDay, LocalDate today) {
        // 일주일 출석 정보 조회 로직
        return AttendanceStatusResponse.of(attendanceStatus, continuounsDay, today);
    }

    public AttendanceStatusResponse checkAttendance(AttendanceCheckRequest request) {
        // 출석 체크, 보상 지급 로직
        return AttendanceCheckResponse.builder()
                .success(true) // 출석 성공 여부
                .exp(10) // 지급된 경험치
                .point(5) // 지급된 포인트
                .build();
    }

}
