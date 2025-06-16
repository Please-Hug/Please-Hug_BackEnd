package org.example.hugmeexp.domain.attendance.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AttendanceCheckResponse {
    private final boolean success;
    private final int exp;
    private final int point;
}
