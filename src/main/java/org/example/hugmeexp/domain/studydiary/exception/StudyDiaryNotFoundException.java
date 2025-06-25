package org.example.hugmeexp.domain.studydiary.exception;

import org.example.hugmeexp.global.common.exception.BaseCustomException;
import org.springframework.http.HttpStatus;

public class StudyDiaryNotFoundException extends BaseCustomException {

    public StudyDiaryNotFoundException() {
        super(HttpStatus.NOT_FOUND, "존재하지 않는 배움일기입니다.", 3002);
    }
}
