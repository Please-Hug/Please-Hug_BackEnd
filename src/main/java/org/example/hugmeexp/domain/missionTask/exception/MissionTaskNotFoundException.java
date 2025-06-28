package org.example.hugmeexp.domain.missionTask.exception;

import org.example.hugmeexp.global.common.exception.BaseCustomException;
import org.springframework.http.HttpStatus;

public class MissionTaskNotFoundException extends BaseCustomException {
    public MissionTaskNotFoundException() {
        super(HttpStatus.NOT_FOUND, "미션 태스크를 찾을 수 없습니다.");
    }
}
