package org.example.hugmeexp.domain.mission.exception;

import org.example.hugmeexp.global.common.exception.BaseCustomException;
import org.springframework.http.HttpStatus;

public class AlreadyExistsUserMissionException extends BaseCustomException {
    public AlreadyExistsUserMissionException() {
        super(HttpStatus.CONFLICT, "이미 존재하는 유저 미션입니다.");
    }
}
