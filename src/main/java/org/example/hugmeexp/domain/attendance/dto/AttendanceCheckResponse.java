package org.example.hugmeexp.domain.attendance.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AttendanceCheckResponse {
    private final boolean attend;
    private final int exp;
    private final int point;

    public static AttendanceCheckResponse of(boolean attend, int exp, int point) {
        return new AttendanceCheckResponse(attend, exp, point);
    }

    private AttendanceCheckResponse(boolean attend, int exp, int point) {
        this.attend = attend;
        this.exp = exp;
        this.point = point;
    }
}
