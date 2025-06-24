package org.example.hugmeexp.domain.praise.exception;

import org.example.hugmeexp.global.common.exception.BaseCustomException;
import org.springframework.http.HttpStatus;

public class UnauthorizedEmojiDeleteException extends BaseCustomException {
    public UnauthorizedEmojiDeleteException() {
        super(HttpStatus.FORBIDDEN,"이모지 삭제 권한이 없습니다",403);
    }
}
