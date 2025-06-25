package org.example.hugmeexp.domain.attendance.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.hugmeexp.config.TestSecurityConfig;
import org.example.hugmeexp.domain.attendance.dto.AttendanceCheckRequest;
import org.example.hugmeexp.domain.attendance.dto.AttendanceCheckResponse;
import org.example.hugmeexp.domain.attendance.dto.AttendanceStatusResponse;
import org.example.hugmeexp.domain.attendance.service.AttendanceService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AttendanceController.class)
@Import(TestSecurityConfig.class)
class AttendanceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AttendanceService attendanceService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("출석 상태 조회 API 테스트")
    void getAttendanceStatus() throws Exception {
        // given
        Long userId = 1L;
        List<Boolean> attendanceStatus = List.of(true, false, false, false, false, false, false);
        int continuousDay = 1;
        LocalDate today = LocalDate.of(2024, 6, 19);
        AttendanceStatusResponse response = AttendanceStatusResponse.of(attendanceStatus, continuousDay, today);
        Mockito.when(attendanceService.getAttendanceStatus(userId)).thenReturn(response);

        // when & then
        mockMvc.perform(get("/api/v1/attendance")
                        .param("userId", String.valueOf(userId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.attendanceStatus[0]").value(true))
                .andExpect(jsonPath("$.continuousDay").value(1))
                .andExpect(jsonPath("$.today").value("2024-06-19"));
    }

    @Test
    @DisplayName("출석 체크 API 테스트")
    void checkAttendance() throws Exception {
        // given
        Long userId = 1L;
        AttendanceCheckRequest request = new AttendanceCheckRequest(userId);
//        AttendanceCheckResponse response = AttendanceCheckResponse.of(true, 2, 100);
        AttendanceCheckResponse response = new AttendanceCheckResponse(true, 2, 100);
        Mockito.when(attendanceService.checkAttendance(eq(userId))).thenReturn(response); //any(AttendanceCheckRequest.class)

        // when & then
        mockMvc.perform(post("/api/v1/attendance/check")
                        .param("userId", String.valueOf(userId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.attend").value(true))
                .andExpect(jsonPath("$.exp").value(2))
                .andExpect(jsonPath("$.point").value(100));
    }
}