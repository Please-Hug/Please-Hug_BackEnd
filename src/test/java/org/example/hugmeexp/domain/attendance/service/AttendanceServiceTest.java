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
    class GetAllAttendanceDates {

        @Test @DisplayName("유저 없음 예외 처리 → AttendanceUserNotFoundException")
        void userNotFound() {
            when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

            assertThrows(AttendanceUserNotFoundException.class,
                    () -> attendanceService.getAllAttendanceDates(username));
        }

        @Test @DisplayName("유효한 유저 → 날짜 리스트 List<LocalDate> 반환")
        void success() {
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
            when(user.getUsername()).thenReturn(username);
            when(user.getCreatedAt()).thenReturn(LocalDateTime.now().minusDays(10));
            when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

            LocalDate today = LocalDate.now();
            LocalDate weekStart = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));
            LocalDate weekEnd = weekStart.plusDays(6);

            // 주간 기록 스텁
            List<Attendance> weekRecords = List.of(
                    Attendance.of(user, weekStart.plusDays(1)),
                    Attendance.of(user, weekStart.plusDays(3))
            );
            when(attendanceRepository
                    .findByUser_UsernameAndAttendanceDateBetween(username, weekStart, weekEnd))
                    .thenReturn(weekRecords);

            // 연속 출석일 계산용 스텁 (가입일부터 오늘까지)
            LocalDate userSince = user.getCreatedAt().toLocalDate();
            List<Attendance> allRecords = List.of(
                    Attendance.of(user, today),
                    Attendance.of(user, today.minusDays(1))
            );
            when(attendanceRepository
                    .findByUser_UsernameAndAttendanceDateBetween(username, userSince, today))
                    .thenReturn(allRecords);

            AttendanceStatusResponse resp = attendanceService.getAttendanceStatus(username);

            assertEquals(7, resp.getAttendanceStatus().size());
            assertEquals(2, resp.getContinuousDay());
            assertEquals(today, resp.getToday());
        }

        @Test @DisplayName("연속 출석일 경계선 테스트: 연속 30일 출석 시 continuousDay == 30")
        void consecutiveThirtyDays() {
            // MockedStatic 밖에서 필요한 날짜들을 미리 계산
            LocalDate fakeToday = LocalDate.of(2025, 6, 30);
            LocalDate userCreatedDate = LocalDate.of(2025, 5, 1); // 60일 전
            LocalDate weekStart = LocalDate.of(2025, 6, 29); // 일요일
            LocalDate weekEnd = LocalDate.of(2025, 7, 5); // 토요일

            try (MockedStatic<LocalDate> md = Mockito.mockStatic(LocalDate.class, Mockito.CALLS_REAL_METHODS)) {
                md.when(LocalDate::now).thenReturn(fakeToday);

                User user = mock(User.class);
                when(user.getUsername()).thenReturn(username);
                when(user.getCreatedAt()).thenReturn(userCreatedDate.atStartOfDay());
                when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

                // 주간 기록 스텁
                when(attendanceRepository
                        .findByUser_UsernameAndAttendanceDateBetween(username, weekStart, weekEnd))
                        .thenReturn(List.of());

                // 연속 출석일 계산용 스텁 - 30일 연속 출석 기록 생성
                List<Attendance> stubs = IntStream.rangeClosed(0, 29)
                        .mapToObj(i -> Attendance.of(user, LocalDate.of(2025, 6, 30).minusDays(i)))
                        .collect(Collectors.toList());
                when(attendanceRepository
                        .findByUser_UsernameAndAttendanceDateBetween(username, userCreatedDate, fakeToday))
                        .thenReturn(stubs);

                AttendanceStatusResponse resp = attendanceService.getAttendanceStatus(username);
                assertEquals(30, resp.getContinuousDay());
            }
        }

        @Test @DisplayName("연속 출석일 경계선 테스트: 연속 365일 출석 시 continuousDay == 365")
        void consecutiveYearDays() {
            // MockedStatic 밖에서 필요한 날짜들을 미리 계산
            LocalDate fakeToday = LocalDate.of(2025, 12, 31);
            LocalDate userCreatedDate = LocalDate.of(2024, 11, 26); // 400일 전
            LocalDate weekStart = LocalDate.of(2025, 12, 28); // 일요일
            LocalDate weekEnd = LocalDate.of(2026, 1, 3); // 토요일

            try (MockedStatic<LocalDate> md = Mockito.mockStatic(LocalDate.class, Mockito.CALLS_REAL_METHODS)) {
                md.when(LocalDate::now).thenReturn(fakeToday);

                User user = mock(User.class);
                when(user.getUsername()).thenReturn(username);
                when(user.getCreatedAt()).thenReturn(userCreatedDate.atStartOfDay());
                when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

                // 주간 기록 스텁
                when(attendanceRepository
                        .findByUser_UsernameAndAttendanceDateBetween(username, weekStart, weekEnd))
                        .thenReturn(List.of());

                // 연속 출석일 계산용 스텁 - 365일 연속 출석 기록 생성
                List<Attendance> stubs = IntStream.rangeClosed(0, 364)
                        .mapToObj(i -> Attendance.of(user, LocalDate.of(2025, 12, 31).minusDays(i)))
                        .collect(Collectors.toList());
                when(attendanceRepository
                        .findByUser_UsernameAndAttendanceDateBetween(username, userCreatedDate, fakeToday))
                        .thenReturn(stubs);

                AttendanceStatusResponse resp = attendanceService.getAttendanceStatus(username);
                assertEquals(365, resp.getContinuousDay());
            }
        }

        @Test
        @DisplayName("월 경계선(2월 28일→3월 1일) 연속 출석 계산")
        void consecutiveAcrossMonthBoundary() {
            // MockedStatic 밖에서 필요한 날짜들을 미리 계산
            LocalDate fakeToday = LocalDate.of(2025, 3, 1);
            LocalDate userCreatedDate = LocalDate.of(2025, 1, 30); // 30일 전
            LocalDate weekStart = LocalDate.of(2025, 2, 23); // 일요일
            LocalDate weekEnd = LocalDate.of(2025, 3, 1); // 토요일
            LocalDate feb28 = LocalDate.of(2025, 2, 28);

            try (MockedStatic<LocalDate> mdl = Mockito.mockStatic(LocalDate.class, Mockito.CALLS_REAL_METHODS)) {
                mdl.when(LocalDate::now).thenReturn(fakeToday);

                User user = mock(User.class);
                when(user.getUsername()).thenReturn(username);
                when(user.getCreatedAt()).thenReturn(userCreatedDate.atStartOfDay());
                when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

                // 주간 기록 스텁
                when(attendanceRepository
                        .findByUser_UsernameAndAttendanceDateBetween(username, weekStart, weekEnd))
                        .thenReturn(List.of());

                // 연속 출석일 계산용 스텁
                List<Attendance> allRecords = List.of(
                        Attendance.of(user, feb28), // 2월 28일
                        Attendance.of(user, fakeToday) // 3월 1일
                );
                when(attendanceRepository
                        .findByUser_UsernameAndAttendanceDateBetween(username, userCreatedDate, fakeToday))
                        .thenReturn(allRecords);

                AttendanceStatusResponse resp = attendanceService.getAttendanceStatus(username);
                assertEquals(2, resp.getContinuousDay(),
                        "2월 28일과 3월 1일을 연속으로 인식해야 합니다.");
            }
        }

        @Test
        @DisplayName("연도 경계선(12월 31일→1월 1일) 연속 출석 계산")
        void consecutiveAcrossYearBoundary() {
            // MockedStatic 밖에서 필요한 날짜들을 미리 계산
            LocalDate fakeToday = LocalDate.of(2025, 1, 1);
            LocalDate userCreatedDate = LocalDate.of(2024, 12, 2); // 30일 전
            LocalDate weekStart = LocalDate.of(2024, 12, 29); // 일요일
            LocalDate weekEnd = LocalDate.of(2025, 1, 4); // 토요일
            LocalDate dec31 = LocalDate.of(2024, 12, 31);

            try (MockedStatic<LocalDate> mdl = Mockito.mockStatic(LocalDate.class, Mockito.CALLS_REAL_METHODS)) {
                mdl.when(LocalDate::now).thenReturn(fakeToday);

                User user = mock(User.class);
                when(user.getUsername()).thenReturn(username);
                when(user.getCreatedAt()).thenReturn(userCreatedDate.atStartOfDay());
                when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

                // 주간 기록 스텁
                when(attendanceRepository
                        .findByUser_UsernameAndAttendanceDateBetween(username, weekStart, weekEnd))
                        .thenReturn(List.of());

                // 연속 출석일 계산용 스텁
                List<Attendance> allRecords = List.of(
                        Attendance.of(user, dec31), // 12월 31일
                        Attendance.of(user, fakeToday) // 1월 1일
                );
                when(attendanceRepository
                        .findByUser_UsernameAndAttendanceDateBetween(username, userCreatedDate, fakeToday))
                        .thenReturn(allRecords);

                AttendanceStatusResponse resp = attendanceService.getAttendanceStatus(username);
                assertEquals(2, resp.getContinuousDay(),
                        "12월 31일과 1월 1일을 연속으로 인식해야 합니다.");
            }
        }
    }

    @Nested @DisplayName("checkAttendance")
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
            when(attendanceRepository.existsByUser_UsernameAndAttendanceDate(eq(username), any(LocalDate.class)))
                    .thenReturn(true);

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

        @Test @DisplayName("동시성 예외 처리 → DataIntegrityViolationException")
        void concurrencyException() {
            User user = mock(User.class);
            when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
            when(attendanceRepository.existsByUser_UsernameAndAttendanceDate(eq(username), any(LocalDate.class)))
                    .thenReturn(false);
            when(attendanceRepository.save(any(Attendance.class)))
                    .thenThrow(new DataIntegrityViolationException("Duplicate entry"));

            assertThrows(AttendanceAlreadyCheckedException.class,
                    () -> attendanceService.checkAttendance(username));
        }
    }
}