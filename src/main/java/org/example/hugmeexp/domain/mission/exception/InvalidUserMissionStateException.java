package org.example.hugmeexp.domain.mission.exception;

import org.example.hugmeexp.global.common.exception.BaseCustomException;
import org.springframework.http.HttpStatus;

public class InvalidUserMissionStateException extends BaseCustomException {
    public InvalidUserMissionStateException() {
        super(HttpStatus.BAD_REQUEST, "보상을 수령하기 위해서는 피드백이 완료되어야 합니다.");
    }
}
