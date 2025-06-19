package org.example.hugmeexp.domain.attendance.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class AttendanceStatusResponse {

    // 일요일~토요일 출석 여부 (true: 출석, false: 결석)
    private final boolean[] attendanceStatus;

    // 연속 출석 일수
    private final int continuousDay;

    // 오늘 날짜
    private final LocalDate today;

    // 정적 팩토리 메서드
    public static AttendanceStatusResponse of(boolean[] attendanceStatus, int continuousDays, LocalDate today) {
        return new AttendanceStatusResponse(attendanceStatus, continuousDays, today);
    }
}
