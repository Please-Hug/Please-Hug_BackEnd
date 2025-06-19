package org.example.hugmeexp.domain.mission.enums;


import lombok.Getter;

@Getter
public enum Difficulty {
    EASY("쉬움"),
    NORMAL("보통"),
    HARD("어려움");

    private final String label;

    Difficulty(String label) {
        this.label = label;
    }

}
