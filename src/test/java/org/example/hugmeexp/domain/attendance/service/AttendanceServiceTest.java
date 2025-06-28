// src/test/java/org/example/hugmeexp/domain/attendance/service/AttendanceServiceTest.java
package org.example.hugmeexp.domain.attendance.service;

import org.example.hugmeexp.domain.attendance.dto.AttendanceCheckResponse;
import org.example.hugmeexp.domain.attendance.dto.AttendanceStatusResponse;
import org.example.hugmeexp.domain.attendance.entity.Attendance;
import org.example.hugmeexp.domain.attendance.exception.AttendanceAlreadyCheckedException;
import org.example.hugmeexp.domain.attendance.exception.AttendanceUserNotFoundException;
import org.example.hugmeexp.domain.attendance.exception.InvalidValueException;
import org.example.hugmeexp.domain.attendance.policy.RewardPolicy;
import org.example.hugmeexp.domain.attendance.repository.AttendanceRepository;
import org.example.hugmeexp.domain.attendance.validation.UsernameValidator;
import org.example.hugmeexp.domain.user.entity.User;
import org.example.hugmeexp.domain.user.repository.UserRepository;
import org.example.hugmeexp.domain.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("AttendanceService 테스트")
class AttendanceServiceTest {

    @Mock
    private AttendanceRepository attendanceRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserService userService;
    @InjectMocks
    private AttendanceService attendanceService;
    @Mock
    private RewardPolicy rewardPolicy;

    private final String username = "user1";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Nested
    @DisplayName("getAttendanceStatus")
    class GetAttendanceStatus {
        @Test
        @DisplayName("존재하지 않는 유저")
        void userNotFound() {
            String notExistUsername = "not_exist_user";
            when(userRepository.findByUsername(notExistUsername)).thenReturn(Optional.empty());
            assertThrows(AttendanceUserNotFoundException.class, () -> attendanceService.getAttendanceStatus(notExistUsername));
        }

        @Test
        @DisplayName("출석 데이터 없음")
        void noAttendanceData() {
            User user = User.builder().username(username).build();
            when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
            when(attendanceRepository.findByUser_UsernameAndAttendanceDateBetween(any(), any(), any()))
                    .thenReturn(Collections.emptyList());
            AttendanceStatusResponse res = attendanceService.getAttendanceStatus(username);
            assertNotNull(res);
            assertEquals(0, res.getContinuousDay());
        }
    }

    @Nested
    @DisplayName("checkAttendance")
    class CheckAttendance {
        @Test
        @DisplayName("이미 출석한 경우")
        void alreadyChecked() {
            User user = User.builder().username(username).build();
            when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
            when(attendanceRepository.existsByUser_UsernameAndAttendanceDate(eq(username), any()))
                    .thenReturn(true);
            assertThrows(AttendanceAlreadyCheckedException.class, () -> attendanceService.checkAttendance(username));
        }

        @Test
        @DisplayName("정상 출석")
        void normalCheck() {
            User user = User.builder().username(username).build();
            when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
            when(attendanceRepository.existsByUser_UsernameAndAttendanceDate(eq(username), any()))
                    .thenReturn(false);
            when(attendanceRepository.save(any())).thenReturn(Attendance.builder().build());
            AttendanceCheckResponse res = attendanceService.checkAttendance(username);
            assertTrue(res.isAttend());
        }
    }

    @Nested
    @DisplayName("getAllAttendanceDates")
    class GetAllAttendanceDates {
        @Test
        @DisplayName("유저 없음")
        void userNotFound() {
            when(userRepository.findByUsername(username)).thenReturn(Optional.empty());
            assertThrows(AttendanceUserNotFoundException.class, () -> attendanceService.getAllAttendanceDates(username));
        }

        @Test
        @DisplayName("대량 데이터")
        void hugeData() {
            User user = User.builder().username(username).build();
            when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
            java.util.List<java.time.LocalDate> dates = new java.util.ArrayList<>();
            for (int i = 0; i < 10000; i++) {
                dates.add(LocalDate.of(2025, 1, 1).plusDays(i));
            }
            when(attendanceRepository.findDatesByUsername(eq(username))).thenReturn(dates);
            java.util.List<java.time.LocalDate> result = attendanceService.getAllAttendanceDates(username);
            assertEquals(10000, result.size());
        }
    }

    // 연속 출석일 경계값 테스트
    @Test
    @DisplayName("연속 출석일 30일 경계값")
    void continuousDaysBoundary30() {
        User user = User.builder().username(username).build();
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        LocalDate today = LocalDate.now();
        List<Attendance> records = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            records.add(Attendance.builder().attendanceDate(today.minusDays(i)).build());
        }
        when(attendanceRepository.findByUser_UsernameAndAttendanceDateBetween(eq(username), any(), any()))
                .thenReturn(records);
        AttendanceStatusResponse res = attendanceService.getAttendanceStatus(username);
        assertEquals(30, res.getContinuousDay());
    }

    @Test
    @DisplayName("연속 출석일 365일 경계값")
    void continuousDaysBoundary365() {
        User user = User.builder().username(username).build();
        // createdAt 값 세팅
        java.lang.reflect.Field createdAtField;
        try {
            createdAtField = user.getClass().getSuperclass().getDeclaredField("createdAt");
            createdAtField.setAccessible(true);
            createdAtField.set(user, LocalDateTime.now().minusYears(2));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        LocalDate today = LocalDate.now();
        List<Attendance> records = new ArrayList<>();
        for (int i = 0; i < 365; i++) {
            records.add(Attendance.builder().attendanceDate(today.minusDays(i)).build());
        }
        when(attendanceRepository.findByUser_UsernameAndAttendanceDateBetween(eq(username), any(), any()))
                .thenReturn(records);
        AttendanceStatusResponse res = attendanceService.getAttendanceStatus(username);
        assertEquals(365, res.getContinuousDay());
    }

    @Test
    @DisplayName("한 주 중 일부만 출석했을 때 출석 상태 리스트 확인")
    void partialWeekAttendance() {
        User user = User.builder().username(username).build();
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        LocalDate today = LocalDate.now();
        LocalDate weekStart = today.with(java.time.temporal.TemporalAdjusters.previousOrSame(java.time.DayOfWeek.SUNDAY));
        List<Attendance> records = List.of(
                Attendance.builder().attendanceDate(weekStart.plusDays(1)).build(),
                Attendance.builder().attendanceDate(weekStart.plusDays(3)).build()
        );
        when(attendanceRepository.findByUser_UsernameAndAttendanceDateBetween(eq(username), any(), any()))
                .thenReturn(records);
        AttendanceStatusResponse res = attendanceService.getAttendanceStatus(username);
        List<Boolean> status = res.getAttendanceStatus();
        assertEquals(7, status.size());
        assertTrue(status.get(1));
        assertTrue(status.get(3));
        assertFalse(status.get(0));
        assertFalse(status.get(2));
        assertFalse(status.get(4));
        assertFalse(status.get(5));
        assertFalse(status.get(6));
    }

    @Test
    @DisplayName("출석 저장 중 DB 제약조건 위반시 AttendanceAlreadyCheckedException 발생")
    void checkAttendanceDataIntegrityViolation() {
        User user = User.builder().username(username).build();
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(attendanceRepository.existsByUser_UsernameAndAttendanceDate(eq(username), any()))
                .thenReturn(false);
        when(attendanceRepository.save(any())).thenThrow(new org.springframework.dao.DataIntegrityViolationException("중복"));
        assertThrows(AttendanceAlreadyCheckedException.class, () -> attendanceService.checkAttendance(username));
    }

    @Test
    @DisplayName("출석 데이터가 전혀 없는 유저의 경우 빈 리스트 반환")
    void getAllAttendanceDatesEmpty() {
        User user = User.builder().username(username).build();
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(attendanceRepository.findDatesByUsername(eq(username))).thenReturn(Collections.emptyList());
        List<LocalDate> result = attendanceService.getAllAttendanceDates(username);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("UsernameValidator에서 유효하지 않은 username 입력 시 예외 발생")
    void invalidUsername() {
        String invalidUsername = "";
        assertThrows(InvalidValueException.class, () -> UsernameValidator.validate(invalidUsername));
    }

    @Test
    @DisplayName("checkAttendance - 동시성: 출첵 여러번 시도할 때 한 번만 성공, 나머지는 예외")
    void checkAttendance_concurrent() throws Exception {
        User user = User.builder().username(username).build();
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(attendanceRepository.existsByUser_UsernameAndAttendanceDate(eq(username), any()))
                .thenReturn(false);

        // 첫 번째만 성공, 이후 모두 예외
        AtomicBoolean saved = new AtomicBoolean(false);
        when(attendanceRepository.save(any())).thenAnswer(invocation -> {
            if (saved.compareAndSet(false, true)) {
                return Attendance.builder().build();
            } else {
                throw new org.springframework.dao.DataIntegrityViolationException("중복");
            }
        });

        int threadCount = 10;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        List<Future<Boolean>> results = new ArrayList<>();

        for (int i = 0; i < threadCount; i++) {
            results.add(executor.submit(() -> {
                try {
                    attendanceService.checkAttendance(username);
                    return true;
                } catch (AttendanceAlreadyCheckedException e) {
                    return false;
                } finally {
                    latch.countDown();
                }
            }));
        }
        latch.await();
        executor.shutdown();

        long successCount = results.stream().filter(f -> {
            try { return f.get(); } catch (Exception e) { return false; }
        }).count();
        assertEquals(1, successCount, "동시성 환경에서 한 번만 성공해야 함");
    }

}