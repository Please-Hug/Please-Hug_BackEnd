// src/test/java/org/example/hugmeexp/domain/attendance/controller/AttendanceControllerTest.java
package org.example.hugmeexp.domain.attendance.controller;

import org.example.hugmeexp.domain.attendance.dto.AttendanceCheckResponse;
import org.example.hugmeexp.domain.attendance.dto.AttendanceStatusResponse;
import org.example.hugmeexp.domain.attendance.exception.AttendanceAlreadyCheckedException;
import org.example.hugmeexp.domain.attendance.exception.AttendanceUserNotFoundException;
import org.example.hugmeexp.domain.attendance.service.AttendanceService;
import org.example.hugmeexp.global.common.exception.ExceptionController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.method.annotation.AuthenticationPrincipalArgumentResolver;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AttendanceController 테스트")
class AttendanceControllerTest {

    private static final String BASE_URL = "/api/v1/attendance";

    private MockMvc mockMvc;

    @Mock
    private AttendanceService attendanceService;

    @InjectMocks
    private AttendanceController attendanceController;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(attendanceController)
                .setControllerAdvice(new ExceptionController())
                .setCustomArgumentResolvers(new AuthenticationPrincipalArgumentResolver())
                .build();
    }

    @Test
    @DisplayName("POST /check - 성공")
    void checkAttendance_success() throws Exception {
        String username = "testUser";
        AttendanceCheckResponse dto = AttendanceCheckResponse.builder()
                .attend(true).exp(31).point(1).build();
        when(attendanceService.checkAttendance(username)).thenReturn(dto);

        // UserDetails + Authentication 준비
        UserDetails userDetails = User.withUsername(username)
                .password("dummy")
                .authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")))
                .build();

        Authentication auth = new UsernamePasswordAuthenticationToken(
                userDetails,
                userDetails.getPassword(),
                userDetails.getAuthorities()
        );

        // SecurityContextHolder 에도 넣어줘야 @AuthenticationPrincipal 바인딩이 된다.
        SecurityContext ctx = new SecurityContextImpl();
        ctx.setAuthentication(auth);
        SecurityContextHolder.setContext(ctx);

        mockMvc.perform(post(BASE_URL + "/check")
                        .principal(auth)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.attend").value(true))
                .andExpect(jsonPath("$.data.exp").value(31))
                .andExpect(jsonPath("$.data.point").value(1))
                .andExpect(jsonPath("$.message").value("Attendance check success"));
    }

    @Test
    @DisplayName("GET /status - 성공")
    void getAttendanceStatus_success() throws Exception {
        String username = "testUser";
        LocalDate today = LocalDate.now();
        List<Boolean> statusList = Arrays.asList(true, false, true, false, false, false, false);
        AttendanceStatusResponse dto = AttendanceStatusResponse.of(statusList, 1, today);
        when(attendanceService.getAttendanceStatus(username)).thenReturn(dto);

        // UserDetails + Authentication 준비
        UserDetails userDetails = User.withUsername(username)
                .password("dummy")
                .authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")))
                .build();

        Authentication auth = new UsernamePasswordAuthenticationToken(
                userDetails,
                userDetails.getPassword(),
                userDetails.getAuthorities()
        );

        // SecurityContextHolder 에도 넣어줘야 @AuthenticationPrincipal 바인딩이 된다.
        SecurityContext ctx = new SecurityContextImpl();
        ctx.setAuthentication(auth);
        SecurityContextHolder.setContext(ctx);

        mockMvc.perform(get(BASE_URL + "/status")
                        .principal(auth)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.attendanceStatus.length()").value(7))
                .andExpect(jsonPath("$.data.continuousDay").value(1))
                .andExpect(jsonPath("$.data.today").value(today.toString()))
                .andExpect(jsonPath("$.message").value("Attendance status retrieved successfully"));
    }

    @Test
    @DisplayName("GET /dates - 성공")
    void getAllDates_success() throws Exception {
        String username = "testUser";
        // ★ 여기서 List<LocalDate> 로 반환 스텁을 설정합니다.
        List<LocalDate> dates = Arrays.asList(
                LocalDate.of(2025, 6, 1),
                LocalDate.of(2025, 6, 2)
        );
        when(attendanceService.getAllAttendanceDates(username)).thenReturn(dates);

        // UserDetails + Authentication 준비
        UserDetails userDetails = User.withUsername(username)
                .password("dummy")
                .authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")))
                .build();

        Authentication auth = new UsernamePasswordAuthenticationToken(
                userDetails,
                userDetails.getPassword(),
                userDetails.getAuthorities()
        );

        // SecurityContextHolder 에도 넣어줘야 @AuthenticationPrincipal 바인딩이 된다.
        SecurityContext ctx = new SecurityContextImpl();
        ctx.setAuthentication(auth);
        SecurityContextHolder.setContext(ctx);

        mockMvc.perform(get(BASE_URL + "/dates")
                        .principal(auth)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                // 반환된 LocalDate들이 문자열로 직렬화됩니다.
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0]").value("2025-06-01"))
                .andExpect(jsonPath("$.data[1]").value("2025-06-02"))
                .andExpect(jsonPath("$.message").value("all Attendance dates retrieved success"));
    }

    @Test
    @DisplayName("POST /check - 이미 출석함 → 409")
    void checkAttendance_alreadyChecked() throws Exception {
        String username = "testUser";
        when(attendanceService.checkAttendance(username))
                .thenThrow(new AttendanceAlreadyCheckedException());

        // UserDetails + Authentication 준비
        UserDetails userDetails = User.withUsername(username)
                .password("dummy")
                .authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")))
                .build();

        Authentication auth = new UsernamePasswordAuthenticationToken(
                userDetails,
                userDetails.getPassword(),
                userDetails.getAuthorities()
        );

        // SecurityContextHolder 에도 넣어줘야 @AuthenticationPrincipal 바인딩이 된다.
        SecurityContext ctx = new SecurityContextImpl();
        ctx.setAuthentication(auth);
        SecurityContextHolder.setContext(ctx);

        mockMvc.perform(post(BASE_URL + "/check")
                        .principal(auth)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("GET /status - 유저 없음 → 404")
    void getAttendanceStatus_userNotFound() throws Exception {
        String username = "testUser";
        when(attendanceService.getAttendanceStatus(username))
                .thenThrow(new AttendanceUserNotFoundException());

        // UserDetails + Authentication 준비
        UserDetails userDetails = User.withUsername(username)
                .password("dummy")
                .authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")))
                .build();

        Authentication auth = new UsernamePasswordAuthenticationToken(
                userDetails,
                userDetails.getPassword(),
                userDetails.getAuthorities()
        );

        // SecurityContextHolder 에도 넣어줘야 @AuthenticationPrincipal 바인딩이 된다.
        SecurityContext ctx = new SecurityContextImpl();
        ctx.setAuthentication(auth);
        SecurityContextHolder.setContext(ctx);

        mockMvc.perform(get(BASE_URL + "/status")
                        .principal(auth)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /dates - 유저 없음 → 404")
    void getAllDates_userNotFound() throws Exception {
        String username = "testUser";
        when(attendanceService.getAllAttendanceDates(username))
                .thenThrow(new AttendanceUserNotFoundException());

        // UserDetails + Authentication 준비
        UserDetails userDetails = User.withUsername(username)
                .password("dummy")
                .authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")))
                .build();

        Authentication auth = new UsernamePasswordAuthenticationToken(
                userDetails,
                userDetails.getPassword(),
                userDetails.getAuthorities()
        );

        // SecurityContextHolder 에도 넣어줘야 @AuthenticationPrincipal 바인딩이 된다.
        SecurityContext ctx = new SecurityContextImpl();
        ctx.setAuthentication(auth);
        SecurityContextHolder.setContext(ctx);

        mockMvc.perform(get(BASE_URL + "/dates")
                        .principal(auth)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
