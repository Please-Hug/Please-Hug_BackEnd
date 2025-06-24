package org.example.hugmeexp.domain.attendance.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceStatusResponse {
    // 일요일~토요일 출석 여부 (true: 출석, false: 결석)
    private boolean[] attendanceStatus;
    // 연속 출석 일수
    private int continuousDay;
    // 오늘 날짜
    private LocalDate today;

    // 정적 팩토리 메서드
    public static AttendanceStatusResponse of(boolean[] attendanceStatus, int continuousDay, LocalDate today) {
        return AttendanceStatusResponse.builder()
                .attendanceStatus(attendanceStatus)
                .continuousDay(continuousDay)
                .today(today)
                .build();
    }

}
