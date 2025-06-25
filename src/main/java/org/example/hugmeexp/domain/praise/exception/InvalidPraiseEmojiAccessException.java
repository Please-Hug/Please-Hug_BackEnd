package org.example.hugmeexp.domain.praise.exception;

import org.example.hugmeexp.global.common.exception.BaseCustomException;
import org.springframework.http.HttpStatus;

public class InvalidPraiseEmojiAccessException extends BaseCustomException {
    public InvalidPraiseEmojiAccessException() {
        super(HttpStatus.BAD_REQUEST,"칭찬 게시물의 실제 반응과 일치하지 않습니다.",400);
    }
}
