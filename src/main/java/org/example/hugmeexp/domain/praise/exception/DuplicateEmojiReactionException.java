package org.example.hugmeexp.domain.praise.exception;

import org.example.hugmeexp.global.common.exception.BaseCustomException;
import org.springframework.http.HttpStatus;

public class DuplicateEmojiReactionException extends BaseCustomException {
    public DuplicateEmojiReactionException() {
        super(HttpStatus.CONFLICT,"이미 반응을 한 이모지 입니다",409);
    }
}
