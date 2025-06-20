package org.example.hugmeexp.domain.mission.exception;

import org.example.hugmeexp.global.common.exception.BaseCustomException;
import org.springframework.http.HttpStatus;

public class UserMissionNotFoundException extends BaseCustomException {
    public UserMissionNotFoundException() {
        super(HttpStatus.NOT_FOUND, "유저 미션을 찾을 수 없습니다.");
    }
}
