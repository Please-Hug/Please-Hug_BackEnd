package org.example.hugmeexp.domain.attendance.service;


import lombok.RequiredArgsConstructor;
import org.example.hugmeexp.domain.attendance.dto.AttendanceCheckResponse;
import org.example.hugmeexp.domain.attendance.dto.AttendanceStatusResponse;
import org.example.hugmeexp.domain.attendance.entity.Attendance;
import org.example.hugmeexp.domain.attendance.exception.AttendanceAlreadyCheckedException;
import org.example.hugmeexp.domain.attendance.exception.AttendanceUserNotFoundException;
import org.example.hugmeexp.domain.attendance.repository.AttendanceRepository;
import org.example.hugmeexp.domain.user.repository.UserRepository;
import org.example.hugmeexp.domain.user.entity.User;
import org.example.hugmeexp.domain.user.service.UserService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final UserRepository userRepository;
    private final UserService userService;

    @Transactional(readOnly = true)
    public List<LocalDate> getAllAttendanceDates(String username) {

        // 1) 사용자 존재 확인
        userRepository.findByUsername(username)
                .orElseThrow(AttendanceUserNotFoundException::new);

        // 2) 날짜 리스트만 조회
        return attendanceRepository.findDatesByUsername(username);
    }

    // 출석 상태 조회 (일요일~토요일의 일주일, 연속 출석일)
    @Transactional(readOnly = true)
    public AttendanceStatusResponse getAttendanceStatus(String username) {

        // 유저 존재 확인 및 예외처리 추가
        userRepository.findByUsername(username)
                .orElseThrow(AttendanceUserNotFoundException::new);

        LocalDate today = LocalDate.now();

        // 한 주의 시작(일요일)과 끝(토요일) 날짜 계산
        LocalDate weekStart =
                today.with(java.time.temporal.TemporalAdjusters.previousOrSame(java.time.DayOfWeek.SUNDAY));
        LocalDate weekEnd = weekStart.plusDays(6);

        // DB에서 최근 7일간 출석 기록 조회
        List<Attendance> records = attendanceRepository
                .findByUser_UsernameAndAttendanceDateBetween(username, weekStart, weekEnd);

        // 출석한 날짜만 추출
        Set<LocalDate> dates = records.stream()
                .map(Attendance::getAttendanceDate)
                .collect(Collectors.toSet());

        // 일요일~토요일 출석 여부를 List<boolean>로 변환
        List<Boolean> attendanceStatus = IntStream.range(0, 7)
                .mapToObj(i -> dates.contains(weekStart.plusDays(i)))
                .collect(Collectors.toList());

        // 연속 출석일 계산 호출
        int continuousDay = calculateContinuousDays(username, today);

        return AttendanceStatusResponse.of(attendanceStatus, continuousDay, today);
    }

    /* 연속 출석일 계산 메소드, 유저 가입일 null-safe 처리
    계산 기준은 아래와 같음

     1. 최근 30일 연속
     2. 30일 이상일 때 최근 1년 연속
     3. 1년 이상일 때 가입일 이후 전체 연속
     */
    private int calculateContinuousDays(String username, LocalDate today) {

        // 과거 1달 출석일 전체 조회
        LocalDate oneMonthAgo = today.minusDays(29);
        Set<LocalDate> monthDates = attendanceRepository
                .findByUser_UsernameAndAttendanceDateBetween(username, oneMonthAgo, today)
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
                .findByUser_UsernameAndAttendanceDateBetween(username, oneYearAgo, today)
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

        // 유저의 Null-safe 가입일 조회, User.getCreatedAt()이 null이더라도 예외나 NPE가 발생하지 않도록 기본값을 지정해 주는 처리 방식
        LocalDate userSince = Optional.ofNullable(
                        userRepository.findByUsername(username)
                                .orElseThrow(AttendanceUserNotFoundException::new)
                                .getCreatedAt()
                )
                .map(LocalDateTime::toLocalDate)
                .orElse(today);  // 유저 가입일 null인 경우 오늘부터 조회해서, 오늘 출석이 있으면 연속 1일로 처리

        // 1년 이상 연속 출석 시, 가입일 이후의 전체 출석기록 조회
        Set<LocalDate> allDates = attendanceRepository
                .findByUser_UsernameAndAttendanceDateBetween(username, userSince, today)
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


    // 출석 체크
        @Transactional
        public AttendanceCheckResponse checkAttendance (String username) {

            LocalDate today = LocalDate.now();

            // 사용자 존재 확인
           User user = userRepository.findByUsername(username)
                    .orElseThrow(AttendanceUserNotFoundException::new);

            // 이미 출첵했는지 확인
            if (attendanceRepository.existsByUser_UsernameAndAttendanceDate(username, today)) {
                throw new AttendanceAlreadyCheckedException();
            }

            int exp = 31;
            int point = 1;

            try{
                // 신규 출석 저장
                Attendance attendance = Attendance.of(user, today);
                attendanceRepository.save(attendance);
            } catch (DataIntegrityViolationException e){
                throw new AttendanceAlreadyCheckedException();
            }

            userService.increaseExp(user, exp);
            userService.increasePoint(user, point);

            return AttendanceCheckResponse.builder()
                    .attend(true)
                    .exp(exp)
                    .point(point)
                    .build();

        }

    }