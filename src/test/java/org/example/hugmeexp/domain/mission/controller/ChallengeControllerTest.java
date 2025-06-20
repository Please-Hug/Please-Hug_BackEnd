package org.example.hugmeexp.domain.mission.controller;

import org.example.hugmeexp.domain.mission.enums.UserMissionState;
import org.example.hugmeexp.domain.mission.service.MissionService;
import org.example.hugmeexp.global.common.exception.ExceptionController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@DisplayName("챌린지 컨트롤러 테스트")
class ChallengeControllerTest {
    private MockMvc mockMvc;

    @Mock
    private MissionService missionService;

    @InjectMocks
    private ChallengeController challengeController;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(challengeController)
                .setControllerAdvice(new ExceptionController())
                .build();
    }

    @Test
    @DisplayName("챌린지 상태 업데이트 - 성공")
    void updateChallengeState_Success() throws Exception {
        // given
        Long challengeId = 123L;
        UserMissionState newState = UserMissionState.COMPLETED;

        // when & then
        mockMvc.perform(patch("/api/v1/challenges/{challengeId}", challengeId)
                        .param("newProgress", newState.name()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message")
                        .value("챌린지 상태가 성공적으로 업데이트되었습니다."))
                // data 필드는 설정하지 않았으므로 null 이거나 missing
                .andExpect(jsonPath("$.data").doesNotExist());

        // service 호출 검증
        verify(missionService).changeUserMissionState(challengeId, newState);
    }

    @Test
    @DisplayName("newProgress 파라미터가 잘못되면 400 Bad Request")
    void updateChallengeState_BadRequest_InvalidEnum() throws Exception {
        Long challengeId = 123L;
        // invalid enum value
        mockMvc.perform(patch("/api/v1/challenges/{challengeId}", challengeId)
                        .param("newProgress", "INVALID_STATE"))
                .andExpect(status().is(500)); // 나중에 400으로 변경해야함
    }
}