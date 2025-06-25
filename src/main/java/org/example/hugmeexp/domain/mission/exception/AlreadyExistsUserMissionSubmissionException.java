package org.example.hugmeexp.domain.mission.exception;

import org.example.hugmeexp.global.common.exception.BaseCustomException;
import org.springframework.http.HttpStatus;

public class AlreadyExistsUserMissionSubmissionException extends BaseCustomException {
    public AlreadyExistsUserMissionSubmissionException() {
        super(HttpStatus.CONFLICT, "이미 제출한 미션입니다.");
    }
}
