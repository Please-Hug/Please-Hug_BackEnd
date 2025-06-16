package org.example.hugmeexp.domain.attendance.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AttendanceCheckRequest {

    private final Long userId;

    public AttendanceCheckRequest(Long userId) {
        this.userId = userId;
    }

    public static AttendanceCheckRequest ofCheck(Long userId) {
        return AttendanceCheckRequest.builder()
                .userId(userId)
                .build();
    }

}
