package org.example.hugmeexp.domain.attendance.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.hugmeexp.domain.attendance.dto.AttendanceStatusResponse;
import org.example.hugmeexp.domain.attendance.exception.AttendanceUserNotFoundException;
import org.example.hugmeexp.domain.attendance.exception.InvalidValueException;
import org.example.hugmeexp.domain.attendance.exception.UsernameTooLongException;
import org.example.hugmeexp.domain.attendance.service.AttendanceService;
import org.example.hugmeexp.global.common.exception.ExceptionController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("AttendanceController 테스트")
class AttendanceControllerTest {

    @Mock
    private AttendanceService attendanceService;

    @InjectMocks
    private AttendanceController attendanceController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    protected String baseUrl = "/api/v1/attendance";
    protected String username = "user1";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders
                .standaloneSetup(attendanceController)
                .setControllerAdvice(new ExceptionController())
                .apply(springSecurity())
                .build();
        objectMapper = new ObjectMapper();
    }

    @Nested
    @DisplayName("이상한 요청이 들어왔을 때 테스트")
    class UnHappyCases {


        @Test
        @DisplayName("username이 공백일 때 400 Bad Request")
        void blankUsername() throws Exception {
            mockMvc.perform(get(baseUrl + "/ /status")
                            .with(user("testUser"))
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("매우 긴 username 입력")
        void veryLongUsername() throws Exception {
            String longUsername = "a".repeat(300);
            when(attendanceService.getAttendanceStatus(longUsername))
                    .thenThrow(new UsernameTooLongException());
            mockMvc.perform(get(baseUrl + "/" + longUsername + "/status")
                            .with(user("testUser"))
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("대량 데이터 반환")
        void hugeAttendanceDates() throws Exception {
            List<LocalDate> dates = new java.util.ArrayList<>();
            for (int i = 0; i < 10000; i++) {
                dates.add(LocalDate.of(2025, 1, 1).plusDays(i));
            }
            when(attendanceService.getAllAttendanceDates(username)).thenReturn(dates);

            mockMvc.perform(get(baseUrl + "/" + username + "/dates")
                            .with(user("testUser"))
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data[9999]").value("2052-05-18"));
        }

        @Test
        @DisplayName("서비스가 null 반환 시 500")
        void serviceReturnsNull() throws Exception {
            when(attendanceService.getAttendanceStatus(username)).thenReturn(null);
            mockMvc.perform(get(baseUrl + "/" + username + "/status")
                            .with(user("testUser"))
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isInternalServerError());
        }

        @Test
        @DisplayName("동시성: 여러 번 호출")
        void concurrentRequests() throws Exception {
            when(attendanceService.getAttendanceStatus(username))
                    .thenReturn(AttendanceStatusResponse.of(
                            Arrays.asList(true, false, true), 1, LocalDate.now()));
            ExecutorService executor = Executors.newFixedThreadPool(10);
            for (int i = 0; i < 10; i++) {
                executor.submit(() -> {
                    try {
                        mockMvc.perform(get(baseUrl + "/" + username + "/status")
                                        .with(user("testUser"))
                                        .accept(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk());
                    } catch (Exception ignored) {
                    }
                });
            }
            executor.shutdown();
            Thread.sleep(1000);
            verify(attendanceService, atLeast(1)).getAttendanceStatus(username);
        }


        @Test
        @DisplayName("username이 공백일 때 400 Bad Request")
        void getAttendanceStatus_blankUsername_returnsBadRequest() throws Exception {
            String blankUsername = " ";
            when(attendanceService.getAttendanceStatus(blankUsername))
                    .thenThrow(new InvalidValueException("username must not be blank"));
            mockMvc.perform(get(baseUrl + "/" + blankUsername + "/status")
                            .with(user("testUser"))
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("존재하지 않는 username이면 404 Not Found")
        void getAttendanceStatus_userNotFound_returnsNotFound() throws Exception {
            Mockito.doThrow(new AttendanceUserNotFoundException())
                    .when(attendanceService).getAttendanceStatus(any(String.class));

            mockMvc.perform(get(baseUrl + "/not_exist/status")
                            .with(user("testUser"))
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("getAllAttendanceDates - 대량 데이터 반환")
        void getAllAttendanceDates_paging_api() throws Exception {
            List<LocalDate> dates = new java.util.ArrayList<>();
            for (int i = 0; i < 100; i++) {
                dates.add(LocalDate.of(2025, 1, 1).plusDays(i));
            }
            when(attendanceService.getAllAttendanceDates(username)).thenReturn(dates);

            mockMvc.perform(get(baseUrl + "/" + username + "/dates")
                            .with(user("testUser"))
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON));
        }
    }
}