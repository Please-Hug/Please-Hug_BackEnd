package org.example.hugmeexp.domain.studydiary.exception;

import org.example.hugmeexp.global.common.exception.BaseCustomException;
import org.springframework.http.HttpStatus;

public class LikeNotFoundException extends BaseCustomException {
    public LikeNotFoundException() {
        super(HttpStatus.NOT_FOUND, "좋아요를 찾을 수 없습니다.", 404);
    }
} 