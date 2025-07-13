package org.example.hugmeexp.domain.attendance.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
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

    @Schema(description = "출석한 날짜(yyyy-MM-dd) 리스트, (true: 출석, false: 결석)")
    @Size(min=7, max=7)
    private List<Boolean> attendanceStatus;

    @Schema(description = "연속 출석 일수")
    @Min(0)
    private int continuousDay;

    @Schema(description = "오늘 날짜 (yyyy-MM-dd)")
    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
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
