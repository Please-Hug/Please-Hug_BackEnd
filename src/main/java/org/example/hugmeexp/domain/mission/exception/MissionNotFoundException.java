package org.example.hugmeexp.domain.mission.exception;

import org.example.hugmeexp.global.common.exception.BaseCustomException;
import org.springframework.http.HttpStatus;

public class MissionNotFoundException extends BaseCustomException {
    public MissionNotFoundException() {
        super(HttpStatus.NOT_FOUND, "해당 미션을 찾을 수 없습니다.");
    }
}
