package org.example.hugmeexp.domain.mission.exception;

import org.example.hugmeexp.global.common.exception.BaseCustomException;
import org.springframework.http.HttpStatus;

public class SubMissionInternalException extends BaseCustomException {
    public SubMissionInternalException(String s) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, s);
    }
}
