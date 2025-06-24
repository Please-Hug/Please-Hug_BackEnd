package org.example.hugmeexp.domain.attendance.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.example.hugmeexp.domain.attendance.dto.AttendanceCheckRequest;
import org.example.hugmeexp.domain.attendance.dto.AttendanceCheckResponse;
import org.example.hugmeexp.domain.attendance.dto.AttendanceStatusResponse;
import org.example.hugmeexp.domain.attendance.entity.Attendance;
import org.example.hugmeexp.domain.attendance.repository.AttendanceRepository;
import org.example.hugmeexp.global.common.repository.UserRepository;
import org.example.hugmeexp.global.entity.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final UserRepository userRepository;

    // 출석 상태 조회 (최근 7일, 연속 출석일)
    @Transactional(readOnly = true)
    public AttendanceStatusResponse getAttendanceStatus(Long userId) {
        LocalDate today = LocalDate.now();
        LocalDate weekAgo = today.minusDays(6); // 7일 전 날짜

        // DB에서 최근 7일간 출석 기록 조회
        List<Attendance> records = attendanceRepository
            .findByUserIdAndAttendanceDateBetween(userId, weekAgo, today);

        // 출석한 날짜만 추출
        Set<LocalDate> dates = records.stream()
                .map(Attendance::getAttendanceDate)
                .collect(Collectors.toSet());

        // boolean 배열로 변환, 요일별 출석 여부 채우기
        boolean[] attendanceStatus = new boolean[7];
        for (int i = 0; i < 7; i++) {
            LocalDate date = weekAgo.plusDays(i);
            attendanceStatus[i] = dates.contains(date);
        }

        // 연속 출석일 계산 (오늘부터 과거로)
        int continuousDay = 0;
        for (int i = 0; i < 7; i++) {
            LocalDate date = today.minusDays(i);
            if (dates.contains(date)) {
                continuousDay++;
            } else {
                break;
            }
        }

        return AttendanceStatusResponse.of(attendanceStatus, continuousDay, today);
    }

    // 출석 체크
    @Transactional
    public AttendanceCheckResponse checkAttendance(Long userId, AttendanceCheckRequest request) {
        LocalDate today = LocalDate.now();

        // 오늘 이미 출석체크했는지 확인
        List<Attendance> todayRecords = attendanceRepository
                .findByUserIdAndAttendanceDateBetween(userId, today, today);
        if (!todayRecords.isEmpty()) {
            return AttendanceCheckResponse.builder()
                    .attend(false)
                    .exp(0)
                    .point(0)
                    .build();
        }

        // User repository를 통해 user 객체를 미리 로딩
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + userId));

        // 신규 출석 저장
        Attendance attendance = Attendance.builder()
                .user(user)
                .attendanceDate(today)
                .exp(31)
                .point(1)
                .build();
        attendanceRepository.save(attendance);
ㅌ
        return AttendanceCheckResponse.builder()
                .attend(true)
                .exp(attendance.getExp())
                .point(attendance.getPoint())
                .build();
    }
}