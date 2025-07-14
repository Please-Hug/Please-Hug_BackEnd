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
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
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
    @Cacheable(value = "allAttendanceDates", key = "#username")
    public List<LocalDate> getAllAttendanceDates(String username) {

        // 1) 사용자 존재 확인
        userRepository.findByUsername(username)
                .orElseThrow(AttendanceUserNotFoundException::new);

        // 2) 날짜 리스트만 조회
        return attendanceRepository.findDatesByUsername(username);
    }

    // 출석 상태 조회 (일요일~토요일의 일주일, 연속 출석일)
    @Transactional(readOnly = true)
    @Cacheable(value = "attendanceStatus", key = "#username + '_' + T(java.time.LocalDate).now().toString()")
    public AttendanceStatusResponse getAttendanceStatus(String username) {

        // 사용자 한번만 조회해서 재사용
        User user = userRepository.findByUsername(username)
                .orElseThrow(AttendanceUserNotFoundException::new);

        LocalDate today = LocalDate.now();

        // 한 주의 시작(일요일)과 끝(토요일) 날짜 계산
        LocalDate weekStart =
                today.with(java.time.temporal.TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));
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
        int continuousDay = calculateContinuousDays(user, today);

        return AttendanceStatusResponse.of(attendanceStatus, continuousDay, today);
    }

    /* 연속 출석일 계산 메소드, 가입일로부터 오늘까지 전체 출석기록을 한번에 조회 */
    private int calculateContinuousDays(User user, LocalDate today) {
        LocalDate userSince = Optional.ofNullable(user.getCreatedAt())
                .map(LocalDateTime::toLocalDate)
                .orElseThrow(() -> new IllegalStateException("User creation date is null for user: " + user.getUsername()));

        Set<LocalDate> allDates = attendanceRepository
                .findByUser_UsernameAndAttendanceDateBetween(user.getUsername(), userSince, today)
                .stream()
                .map(Attendance::getAttendanceDate)
                .collect(Collectors.toSet());

        int continuous = 0;
        LocalDate cursor = today;
        while (allDates.contains(cursor) && cursor.isAfter(userSince.minusDays(1))) {
            continuous++;
            cursor = cursor.minusDays(1);
        }

        return continuous;
    }


    // 출석 체크
    @Transactional
    @CacheEvict(value = {"attendanceStatus", "allAttendanceDates"}, key = "#username")
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
            userService.increaseExp(user, exp);
            userService.increasePoint(user, point);

            return AttendanceCheckResponse.builder()
                    .attend(true)
                    .exp(exp)
                    .point(point)
                    .build();
        } catch (DataIntegrityViolationException e){
            throw new AttendanceAlreadyCheckedException();
        }

    }

}