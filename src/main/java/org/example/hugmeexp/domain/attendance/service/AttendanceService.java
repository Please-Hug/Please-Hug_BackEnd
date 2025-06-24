package org.example.hugmeexp.domain.attendance.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.example.hugmeexp.domain.attendance.dto.AttendanceCheckRequest;
import org.example.hugmeexp.domain.attendance.dto.AttendanceCheckResponse;
import org.example.hugmeexp.domain.attendance.dto.AttendanceStatusResponse;
import org.example.hugmeexp.domain.attendance.entity.Attendance;
import org.example.hugmeexp.domain.attendance.exception.AttendanceAlreadyCheckedException;
import org.example.hugmeexp.domain.attendance.exception.AttendanceUserNotFoundException;
import org.example.hugmeexp.domain.attendance.policy.RewardPolicy;
import org.example.hugmeexp.domain.attendance.repository.AttendanceRepository;
import org.example.hugmeexp.global.common.repository.UserRepository;
import org.example.hugmeexp.global.entity.User;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final UserRepository userRepository;
    private final RewardPolicy rewardPolicy;

    // 연속 출석일 계산 메소드
    private int calculateContinuousDays(Long userId, LocalDate today) {

        // 과거 1달 출석일 전체 조회
        LocalDate oneMonthAgo = today.minusDays(29);
        Set<LocalDate> monthDates = attendanceRepository
                .findByUserIdAndAttendanceDateBetween(userId, oneMonthAgo, today)
                .stream()
                .map(Attendance::getAttendanceDate)
                .collect(Collectors.toSet());

        // 1달동안 연속 출석일 계산
        int monthContinuous = 0;
        LocalDate cursor = today;
        while (monthDates.contains(cursor) && monthContinuous < 30) {
            monthContinuous++;
            cursor = cursor.minusDays(1);
        }
        if (monthContinuous < 30) {
            // 1달 미만으로 연속 출석 시 해당 일수 반환
            return monthContinuous;
        }

        // 1년 기준으로 출석일 전체 조회
        LocalDate oneYearAgo = today.minusYears(1);
        Set<LocalDate> yearDates = attendanceRepository
                .findByUserIdAndAttendanceDateBetween(userId, oneYearAgo, today)
                .stream()
                .map(Attendance::getAttendanceDate)
                .collect(Collectors.toSet());


        // 지난 1년동안 연속 출석일 검사
        int yearContinuous = 0;
        cursor = today;
        while (yearDates.contains(cursor) && yearContinuous < 365) {
            yearContinuous++;
            cursor = cursor.minusDays(1);
        }
        if (yearContinuous < 365) {
            // 1년 미만으로 연속 출석 시 해당 일수 반환
            return yearContinuous;
        }
        // 1년 이상 연속 출석 시, 가입일 이후의 전체 출석기록 조회
        User user = userRepository.findById(userId)
                .orElseThrow(AttendanceUserNotFoundException::new);
        LocalDate fullStartDate = user.getCreatedAt().toLocalDate();

        Set<LocalDate> allDates = attendanceRepository
                .findByUserIdAndAttendanceDateBetween(userId, fullStartDate, today)
                .stream()
                .map(Attendance::getAttendanceDate)
                .collect(Collectors.toSet());

        // 가입일 이후 연속 출석일 계산
        int fullContinuous = 0;
        cursor = today;
        while (allDates.contains(cursor) && fullContinuous < 365) {
            fullContinuous++;
            cursor = cursor.minusDays(1);
        }
        return fullContinuous;
    }




    // 출석 상태 조회 (최근 7일, 연속 출석일)
    @Transactional(readOnly = true)
    public AttendanceStatusResponse getAttendanceStatus(Long userId) {
        LocalDate today = LocalDate.now();

        // 한 주의 시작(일요일)과 끝(토요일) 날짜 계산
        LocalDate weekStart =
                today.with(java.time.temporal.TemporalAdjusters.previousOrSame(java.time.DayOfWeek.SUNDAY));
        LocalDate weekEnd = weekStart.plusDays(6);

        // DB에서 최근 7일간 출석 기록 조회
        List<Attendance> records = attendanceRepository
                .findByUserIdAndAttendanceDateBetween(userId, weekStart, weekEnd);

        // 출석한 날짜만 추출
        Set<LocalDate> dates = records.stream()
                .map(Attendance::getAttendanceDate)
                .collect(Collectors.toSet());

        // 일요일~토요일 출석 여부를 List<boolean>로 변환
        List<Boolean> attendanceStatus = IntStream.range(0, 7)
                .mapToObj(i -> dates.contains(weekStart.plusDays(i)))
                .collect(Collectors.toList());

        // 연속 출석일 계산 호출
        int continuousDay = calculateContinuousDays(userId, today);

        return AttendanceStatusResponse.of(attendanceStatus, continuousDay, today);
    }

        // 출석 체크
        @Transactional
        public AttendanceCheckResponse checkAttendance (Long userId){
            LocalDate today = LocalDate.now();

            // 사용자 존재 확인
           userRepository.findById(userId)
                    .orElseThrow(AttendanceUserNotFoundException::new);

            // 이미 출첵했는지 확인
            if (attendanceRepository.existsByUserIdAndAttendanceDate(userId, today)) {
                throw new AttendanceAlreadyCheckedException();
            }

            // 신규 출석 저장
            Attendance attendance = Attendance.builder()
                    .user(userRepository.getById(userId))
                    .attendanceDate(today)
                    .exp(rewardPolicy.getExp()) // 보상 정책은 외부로 분리
                    .point(rewardPolicy.getPoint()) // 보상 정책은 외부로 분리
                    .build();

            try {
                // 출석 정보 저장
                attendanceRepository.save(attendance);
            } catch (DataIntegrityViolationException e) {
                // 중복된 출석 정보 저장 시 예외 처리
                throw new AttendanceAlreadyCheckedException();
            }

            return AttendanceCheckResponse.builder()
                    .attend(true)
                    .exp(attendance.getExp())
                    .point(attendance.getPoint())
                    .build();
        }
    }