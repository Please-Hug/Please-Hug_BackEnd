package org.example.hugmeexp.domain.mission.exception;

import org.example.hugmeexp.global.common.exception.BaseCustomException;
import org.springframework.http.HttpStatus;

public class AlreadyReceivedRewardException extends BaseCustomException {
    public AlreadyReceivedRewardException() {
        super(HttpStatus.BAD_REQUEST, "이미 보상을 수령했습니다.");
    }
}
