package org.example.hugmeexp.domain.attendance.service;

import org.example.hugmeexp.domain.attendance.dto.AttendanceCheckRequest;
import org.example.hugmeexp.domain.attendance.dto.AttendanceCheckResponse;
import org.example.hugmeexp.domain.attendance.dto.AttendanceStatusResponse;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
public class AttendanceService {

    // 예시: 유저별 출석일 저장 (실제 환경에서는 DB 사용)
    private final Map<Long, Set<LocalDate>> attendanceData = new HashMap<>();

    // 일주일 출석 여부 조회
    public AttendanceStatusResponse getAttendanceStatus(Long userId) {
        Set<LocalDate> userAttendance = attendanceData.getOrDefault(userId, new HashSet<>());
        boolean[] attendanceStatus = new boolean[7];
        LocalDate today = LocalDate.now();

        // 최근 7일(오늘 포함) 출석 여부
        for (int i = 0; i < 7; i++) {
            LocalDate date = today.minusDays(6 - i);
            attendanceStatus[i] = userAttendance.contains(date);
        }

        // 연속 출석일 계산 (오늘부터 과거로)
        int continuousDay = 0;
        for (int i = 0; i < 7; i++) {
            LocalDate date = today.minusDays(i);
            if (userAttendance.contains(date)) {
                continuousDay++;
            } else {
                break;
            }
        }

        return AttendanceStatusResponse.of(attendanceStatus, continuousDay, today);
    }

    public AttendanceCheckResponse checkAttendance(Long userId, AttendanceCheckRequest request) {
        Set<LocalDate> userAttendance = attendanceData.computeIfAbsent(userId, k -> new HashSet<>());
        LocalDate today = LocalDate.now();

        boolean alreadyChecked = userAttendance.contains(today);

        if (alreadyChecked) {
            return AttendanceCheckResponse.builder()
                    .attend(false)
                    .exp(0)
                    .point(0)
                    .build();
        }

        // 출석 처리
        userAttendance.add(today);

        return AttendanceCheckResponse.builder()
                .attend(true)
                .exp(31)
                .point(1)
                .build();
    }
}