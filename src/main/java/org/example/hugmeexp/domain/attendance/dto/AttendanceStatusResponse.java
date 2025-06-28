package org.example.hugmeexp.domain.attendance.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceStatusResponse {
    // 일요일~토요일 출석 여부 (true: 출석, false: 결석)
    @Size(min=7, max=7)
    private List<Boolean> attendanceStatus;
    // 연속 출석 일수
    @Min(0)
    private int continuousDay;
    // 오늘 날짜
    @NotNull
    private LocalDate today;

    // 정적 팩토리 메서드
    public static AttendanceStatusResponse of(List<Boolean> attendanceStatus, int continuousDay, LocalDate today) {
        return AttendanceStatusResponse.builder()
                .attendanceStatus(attendanceStatus)
                .continuousDay(continuousDay)
                .today(today)
                .build();
    }

}
