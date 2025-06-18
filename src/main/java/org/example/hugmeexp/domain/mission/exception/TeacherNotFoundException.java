package org.example.hugmeexp.domain.mission.exception;

import org.example.hugmeexp.global.common.exception.BaseCustomException;
import org.springframework.http.HttpStatus;

public class TeacherNotFoundException extends BaseCustomException {
    public TeacherNotFoundException() {
        super(HttpStatus.NOT_FOUND, "해당 선생님을 찾을 수 없습니다.");
    }
}
