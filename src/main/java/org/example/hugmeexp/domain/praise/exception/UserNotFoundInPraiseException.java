package org.example.hugmeexp.domain.praise.exception;

import org.example.hugmeexp.global.common.exception.BaseCustomException;
import org.springframework.http.HttpStatus;

public class UserNotFoundInPraiseException extends BaseCustomException {

    public UserNotFoundInPraiseException(){
        super(HttpStatus.NOT_FOUND,"사용자를 찾을 수 없습니다",404);
    }
}
