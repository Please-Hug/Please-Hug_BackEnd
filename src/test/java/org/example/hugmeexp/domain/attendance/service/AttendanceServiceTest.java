package org.example.hugmeexp.domain.attendance.service;

import org.example.hugmeexp.domain.attendance.dto.AttendanceCheckRequest;
import org.example.hugmeexp.domain.attendance.dto.AttendanceCheckResponse;
import org.example.hugmeexp.domain.attendance.dto.AttendanceStatusResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class AttendanceServiceTest {

    @InjectMocks
    AttendanceService service;

    @Test
    @DisplayName("출석 상태 조회 서비스 테스트")
    void getAttendanceStatus() {
        // given
//        AttendanceService service = new AttendanceService();
        Long userId = 1L;
        LocalDate today = LocalDate.now();

        // 출석 3일 연속 체크
        for (int i = 0; i < 3; i++) {
            LocalDate date = today.minusDays(i);
            service.checkAttendance(userId);//new AttendanceCheckRequest(userId)
        }

        // when
        AttendanceStatusResponse response = service.getAttendanceStatus(userId);

        // then
        assertThat(response.getContinuousDay()).isGreaterThanOrEqualTo(1);
        assertThat(response.getToday()).isEqualTo(today);
        assertThat(response.getAttendanceStatus()).hasSize(7);
    }

    @Test
    @DisplayName("출석 체크 서비스 테스트 - 첫 출석")
    void checkAttendance_first() {
//        AttendanceService service = new AttendanceService();
        Long userId = 2L;

        AttendanceCheckResponse response = service.checkAttendance(userId);//new AttendanceCheckRequest(userId)

        assertThat(response.isAttend()).isTrue();
        assertThat(response.getExp()).isEqualTo(31);
        assertThat(response.getPoint()).isEqualTo(1);
    }

    @Test
    @DisplayName("출석 체크 서비스 테스트 - 이미 출석한 경우")
    void checkAttendance_alreadyChecked() {
//        AttendanceService service = new AttendanceService();
        Long userId = 3L;

        // 첫 출석
        service.checkAttendance(userId);//new AttendanceCheckRequest(userId)
        // 같은 날 재출석 시도
        AttendanceCheckResponse response = service.checkAttendance(userId);//new AttendanceCheckRequest(userId)

        assertThat(response.isAttend()).isFalse();
        assertThat(response.getExp()).isEqualTo(0);
        assertThat(response.getPoint()).isEqualTo(0);
    }
}