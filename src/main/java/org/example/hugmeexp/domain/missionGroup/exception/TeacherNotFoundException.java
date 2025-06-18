package org.example.hugmeexp.domain.missionGroup.exception;

import org.example.hugmeexp.global.common.exception.BaseCustomException;
import org.springframework.http.HttpStatus;

public class TeacherNotFoundException extends BaseCustomException {
    public TeacherNotFoundException() {
        super(HttpStatus.NOT_FOUND, "해당 강사를 찾을 수 없습니다.");
    }
}
