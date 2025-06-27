package org.example.hugmeexp.domain.user.exception;

import org.example.hugmeexp.global.common.exception.BaseCustomException;
import org.springframework.http.HttpStatus;

public class ProfileImageNotFoundException extends BaseCustomException {
    public ProfileImageNotFoundException() {
        super(HttpStatus.BAD_REQUEST, "삭제할 프로필 이미지가 없습니다.", HttpStatus.BAD_REQUEST.value());
    }
}