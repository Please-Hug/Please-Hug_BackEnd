package org.example.hugmeexp.domain.attendance.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceCheckResponse {

    @Schema(description = "출석체크 여부 (true: 출석, false: 결석)")
    private boolean attend;

    @Schema(description = "획득한 경험치")
    private int exp;

    @Schema(description = "획득한 구름조각 개수")
    private int point;
}
