package org.example.hugmeexp.domain.mission.exception;

public class MissionGroupNotFoundException extends RuntimeException {

    public MissionGroupNotFoundException() {
        super("미션 그룹을 찾을 수 없습니다.");
    }
}
