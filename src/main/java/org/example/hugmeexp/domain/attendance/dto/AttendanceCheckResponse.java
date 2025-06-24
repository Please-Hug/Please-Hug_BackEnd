package org.example.hugmeexp.domain.attendance.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceCheckResponse {
    private boolean attend;
    private int exp;
    private int point;


}
