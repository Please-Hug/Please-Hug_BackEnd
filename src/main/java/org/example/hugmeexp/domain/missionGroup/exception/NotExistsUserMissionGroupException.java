package org.example.hugmeexp.domain.missionGroup.exception;

import org.example.hugmeexp.global.common.exception.BaseCustomException;
import org.springframework.http.HttpStatus;

public class NotExistsUserMissionGroupException extends BaseCustomException {
    public NotExistsUserMissionGroupException() {
        super(HttpStatus.NOT_FOUND, "유저가 해당 미션 그룹에 참여하고 있지 않습니다.");
    }
}
