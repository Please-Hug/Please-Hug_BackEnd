package org.example.hugmeexp.domain.missionTask.enums;

import lombok.Getter;

@Getter
public enum TaskState {
    NOT_STARTED("시작전"),
    IN_PROGRESS("진행중"),
    COMPLETED("완료");

    private final String description;

    TaskState(String description) {
        this.description = description;
    }
}
