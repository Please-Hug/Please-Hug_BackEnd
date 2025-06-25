package org.example.hugmeexp.domain.mission.enums;

import lombok.Getter;

@Getter
public enum UserMissionState {
    NOT_STARTED("시작전"),
    IN_PROGRESS("진행중"),
    ABORTED("중단됨"),
    COMPLETED("완료"),
    IN_FEEDBACK("피드백중"),
    FEEDBACK_COMPLETED("피드백 종료"),
    REWARD_RECEIVED("보상 수령 완료");

    private final String description;

    UserMissionState(String description) {
        this.description = description;
    }
}
