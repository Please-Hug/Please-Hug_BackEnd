package org.example.hugmeexp.domain.missionGroup.exception;

import org.example.hugmeexp.global.common.exception.BaseCustomException;
import org.springframework.http.HttpStatus;

public class UserMissionGroupNotFoundException extends BaseCustomException {
    public UserMissionGroupNotFoundException() {
        super(HttpStatus.NOT_FOUND, "유저 미션 그룹을 찾을 수 없습니다.");
    }
}
