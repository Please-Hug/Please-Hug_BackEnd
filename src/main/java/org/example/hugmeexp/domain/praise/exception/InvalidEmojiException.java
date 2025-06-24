package org.example.hugmeexp.domain.praise.exception;

import org.example.hugmeexp.global.common.exception.BaseCustomException;
import org.springframework.http.HttpStatus;

public class InvalidEmojiException extends BaseCustomException {
    public InvalidEmojiException() {
        super(HttpStatus.BAD_REQUEST,"이모지 형식이 유효하지 않습니다",400);
    }
}
