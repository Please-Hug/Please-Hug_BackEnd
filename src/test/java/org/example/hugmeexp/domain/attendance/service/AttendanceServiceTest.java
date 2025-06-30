// src/test/java/org/example/hugmeexp/domain/attendance/service/AttendanceServiceTest.java
package org.example.hugmeexp.domain.attendance.service;

import org.example.hugmeexp.domain.attendance.dto.AttendanceCheckResponse;
import org.example.hugmeexp.domain.attendance.dto.AttendanceStatusResponse;
import org.example.hugmeexp.domain.attendance.entity.Attendance;
import org.example.hugmeexp.domain.attendance.exception.AttendanceAlreadyCheckedException;
import org.example.hugmeexp.domain.attendance.exception.AttendanceUserNotFoundException;
import org.example.hugmeexp.domain.attendance.repository.AttendanceRepository;
import org.example.hugmeexp.domain.user.entity.User;
import org.example.hugmeexp.domain.user.repository.UserRepository;
import org.example.hugmeexp.domain.user.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AttendanceService 테스트")
class AttendanceServiceTest {

    @Mock private AttendanceRepository attendanceRepository;
    @Mock private UserRepository userRepository;
    @Mock private UserService userService;
    @InjectMocks private AttendanceService attendanceService;

    private final String username = "testUser";

    @Nested @DisplayName("getAllAttendanceDates 테스트")
    // 유저 한명의 모든 출석 날짜를 조회
    class GetAllAttendanceDates {

        @Test @DisplayName("유저 없음 예외 처리 → AttendanceUserNotFoundException")
        void userNotFound() {
            when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

            assertThrows(AttendanceUserNotFoundException.class,
                    () -> attendanceService.getAllAttendanceDates(username));
        }

        @Test @DisplayName("유효한 유저 → 날짜 리스트 List<LocalDate> 반환")
        void success() {
            // User 생성은 mock으로만, getUsername() 스텁은 제거
            User user = mock(User.class);
            when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

            List<LocalDate> dates = List.of(
                    LocalDate.of(2025, 6, 1),
                    LocalDate.of(2025, 6, 2)
            );
            when(attendanceRepository.findDatesByUsername(username)).thenReturn(dates);

            List<LocalDate> result = attendanceService.getAllAttendanceDates(username);
            assertEquals(dates, result);
        }
    }

    @Nested @DisplayName("getAttendanceStatus 테스트")
    // 주간(일요일~토요일) 출석 현황과 연속 출석일 수를 조회
    class GetAttendanceStatus {

        @Test @DisplayName("유저 없음 예외 처리→ AttendanceUserNotFoundException")
        void userNotFound() {
            when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

            assertThrows(AttendanceUserNotFoundException.class,
                    () -> attendanceService.getAttendanceStatus(username));
        }

        @Test @DisplayName("출석 기록 존재 → 올바른 상태 반환")
        void success() {
            User user = mock(User.class);
            when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

            LocalDate today = LocalDate.now();
            LocalDate weekStart = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));
            LocalDate weekEnd = weekStart.plusDays(6);

            List<Attendance> weekRecords = List.of(
                    Attendance.of(user, weekStart.plusDays(1)),
                    Attendance.of(user, weekStart.plusDays(3))
            );
            when(attendanceRepository
                    .findByUser_UsernameAndAttendanceDateBetween(username, weekStart, weekEnd))
                    .thenReturn(weekRecords);

            LocalDate oneMonthAgo = today.minusDays(29);
            List<Attendance> monthRecords = List.of(
                    Attendance.of(user, today),
                    Attendance.of(user, today.minusDays(1))
            );
            when(attendanceRepository
                    .findByUser_UsernameAndAttendanceDateBetween(username, oneMonthAgo, today))
                    .thenReturn(monthRecords);

            AttendanceStatusResponse resp = attendanceService.getAttendanceStatus(username);

            assertEquals(7, resp.getAttendanceStatus().size()); // attendanceStatus의 리스트 크기가 7인지
            assertEquals(2, resp.getContinuousDay()); // 연속 출석일 수가 2인지
            assertEquals(today, resp.getToday()); // 오늘 날짜가 맞는지
        }

        @Test @DisplayName("연속 출석일 경계선 테스트: 연속 30일 출석 시 continuousDay == 30")
        void consecutiveThirtyDays() {
            LocalDate fakeToday = LocalDate.of(2025, 6, 30);
            try (MockedStatic<LocalDate> md = Mockito.mockStatic(LocalDate.class, Mockito.CALLS_REAL_METHODS)) {
                md.when(LocalDate::now).thenReturn(fakeToday);

                User user = mock(User.class);
                when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

                // 한 번만 모든 조회(가입일~오늘) 호출 stub
                List<Attendance> stubs = IntStream.rangeClosed(0, 29)
                        .mapToObj(i -> Attendance.of(user, fakeToday.minusDays(i)))
                        .collect(Collectors.toList());
                when(attendanceRepository
                        .findByUser_UsernameAndAttendanceDateBetween(
                                eq(username),
                                any(LocalDate.class),
                                any(LocalDate.class)))
                        .thenReturn(stubs);

                AttendanceStatusResponse resp = attendanceService.getAttendanceStatus(username);
                assertEquals(30, resp.getContinuousDay());
            }
        }

        @Test @DisplayName("연속 출석일 경계선 테스트: 연속 365일 출석 시 continuousDay == 365")
        void consecutiveYearDays() {
            LocalDate fakeToday = LocalDate.of(2025, 12, 31);
            try (MockedStatic<LocalDate> md = Mockito.mockStatic(LocalDate.class, Mockito.CALLS_REAL_METHODS)) {
                md.when(LocalDate::now).thenReturn(fakeToday);

                User user = mock(User.class);
                when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

                List<Attendance> stubs = IntStream.rangeClosed(0, 364)
                        .mapToObj(i -> Attendance.of(user, fakeToday.minusDays(i)))
                        .collect(Collectors.toList());
                when(attendanceRepository
                        .findByUser_UsernameAndAttendanceDateBetween(
                                eq(username),
                                any(LocalDate.class),
                                any(LocalDate.class)))
                        .thenReturn(stubs);

                AttendanceStatusResponse resp = attendanceService.getAttendanceStatus(username);
                assertEquals(365, resp.getContinuousDay());
            }
        }

        @Test
        @DisplayName("월 경계선(2월 28일→3월 1일) 연속 출석 계산")
        void consecutiveAcrossMonthBoundary() {
            LocalDate fakeToday = LocalDate.of(2025, 3, 1);
            try (MockedStatic<LocalDate> mdl = Mockito.mockStatic(LocalDate.class, Mockito.CALLS_REAL_METHODS)) {
                mdl.when(LocalDate::now).thenReturn(fakeToday);

                User user = mock(User.class);
                when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

                // 주간 기록: 모든 인자 매처 사용
                when(attendanceRepository
                        .findByUser_UsernameAndAttendanceDateBetween(
                                eq(username),
                                any(LocalDate.class),
                                any(LocalDate.class)))
                        .thenReturn(List.of());

                // 월간 기록: 모든 인자 매처 사용
                LocalDate start = fakeToday.minusDays(29);
                List<Attendance> monthRec = List.of(
                        Attendance.of(user, fakeToday.minusDays(1)),
                        Attendance.of(user, fakeToday)
                );
                when(attendanceRepository
                        .findByUser_UsernameAndAttendanceDateBetween(
                                eq(username),
                                eq(start),
                                eq(fakeToday)))
                        .thenReturn(monthRec);

                AttendanceStatusResponse resp = attendanceService.getAttendanceStatus(username);
                assertEquals(2, resp.getContinuousDay(),
                        "2월 28일과 3월 1일을 연속으로 인식해야 합니다.");
            }
        }

        @Test
        @DisplayName("연도 경계선(12월 31일→1월 1일) 연속 출석 계산")
        void consecutiveAcrossYearBoundary() {
            LocalDate fakeToday = LocalDate.of(2025, 1, 1);
            try (MockedStatic<LocalDate> mdl = Mockito.mockStatic(LocalDate.class, Mockito.CALLS_REAL_METHODS)) {
                mdl.when(LocalDate::now).thenReturn(fakeToday);

                User user = mock(User.class);
                when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

                // 주간 기록: 모든 인자 매처 사용
                when(attendanceRepository
                        .findByUser_UsernameAndAttendanceDateBetween(
                                eq(username),
                                any(LocalDate.class),
                                any(LocalDate.class)))
                        .thenReturn(List.of());

                // 월간 기록: 모든 인자 매처 사용
                LocalDate start = fakeToday.minusDays(29);
                List<Attendance> monthRec = List.of(
                        Attendance.of(user, fakeToday.minusDays(1)),
                        Attendance.of(user, fakeToday)
                );
                when(attendanceRepository
                        .findByUser_UsernameAndAttendanceDateBetween(
                                eq(username),
                                eq(start),
                                eq(fakeToday)))
                        .thenReturn(monthRec);

                AttendanceStatusResponse resp = attendanceService.getAttendanceStatus(username);
                assertEquals(2, resp.getContinuousDay(),
                        "12월 31일과 1월 1일을 연속으로 인식해야 합니다.");
            }
        }
    }

    @Nested @DisplayName("checkAttendance")
    // 출석 체크 기능
    class CheckAttendance {

        @Test @DisplayName("유저 없음 예외 처리 → AttendanceUserNotFoundException")
        void userNotFound() {
            when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

            assertThrows(AttendanceUserNotFoundException.class,
                    () -> attendanceService.checkAttendance(username));
        }

        @Test @DisplayName("이미 출석함 예외 처리→ AttendanceAlreadyCheckedException")
        void alreadyChecked() {
            User user = mock(User.class);
            when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
            // 모든 인자에 matcher 사용 (eq + any)
            when(attendanceRepository.existsByUser_UsernameAndAttendanceDate(eq(username), any(LocalDate.class)))
                    .thenReturn(true);

            assertThrows(AttendanceAlreadyCheckedException.class,
                    () -> attendanceService.checkAttendance(username));
        }

        @Test @DisplayName("낙관적 락, 동시성 충돌 시 → AttendanceAlreadyCheckedException")
        void optimisticLockingConflict() {
            User user = mock(User.class);
            when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
            when(attendanceRepository.existsByUser_UsernameAndAttendanceDate(eq(username), any(LocalDate.class)))
                    .thenReturn(false);
            // save()에서 OptimisticLockException 던지도록 모킹
            when(attendanceRepository.save(any(Attendance.class)))
                    .thenThrow(new ObjectOptimisticLockingFailureException(Attendance.class, 1L));

            assertThrows(AttendanceAlreadyCheckedException.class,
                    () -> attendanceService.checkAttendance(username));
        }

        @Test @DisplayName("정상 처리 → exp, point 증가 및 응답 반환")
        void success() {
            User user = mock(User.class);
            when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
            when(attendanceRepository.existsByUser_UsernameAndAttendanceDate(eq(username), any(LocalDate.class)))
                    .thenReturn(false);
            when(attendanceRepository.save(any(Attendance.class)))
                    .thenAnswer(inv -> inv.getArgument(0));

            AttendanceCheckResponse resp = attendanceService.checkAttendance(username);

            assertTrue(resp.isAttend());
            assertEquals(31, resp.getExp());
            assertEquals(1, resp.getPoint());
            verify(userService).increaseExp(user, 31);
            verify(userService).increasePoint(user, 1);
        }
    }
}