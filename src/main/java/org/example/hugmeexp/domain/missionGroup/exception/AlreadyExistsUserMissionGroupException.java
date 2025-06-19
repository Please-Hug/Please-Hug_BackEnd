package org.example.hugmeexp.domain.missionGroup.exception;

import org.example.hugmeexp.global.common.exception.BaseCustomException;
import org.springframework.http.HttpStatus;

public class AlreadyExistsUserMissionGroupException extends BaseCustomException {
    public AlreadyExistsUserMissionGroupException() {
        super(HttpStatus.CONFLICT, "유저가 이미 해당 미션 그룹에 참여하고 있습니다.");
    }
}
