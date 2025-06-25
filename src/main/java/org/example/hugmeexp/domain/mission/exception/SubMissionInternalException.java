package org.example.hugmeexp.domain.mission.exception;

import org.example.hugmeexp.global.common.exception.BaseCustomException;
import org.springframework.http.HttpStatus;

public class SubMissionInternalException extends BaseCustomException {
    public SubMissionInternalException(String message) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, message);
    }

    public SubMissionInternalException() {
        super(HttpStatus.INTERNAL_SERVER_ERROR, "서브미션 처리 중 내부 오류가 발생했습니다.");
    }
}
