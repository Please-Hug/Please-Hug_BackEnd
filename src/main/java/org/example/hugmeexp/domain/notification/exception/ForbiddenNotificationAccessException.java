package org.example.hugmeexp.domain.notification.exception;

import org.example.hugmeexp.global.common.exception.BaseCustomException;
import org.springframework.http.HttpStatus;

public class ForbiddenNotificationAccessException extends BaseCustomException {
    public ForbiddenNotificationAccessException() {
        super(HttpStatus.FORBIDDEN, "본인의 알림만 읽을 수 있습니다.", 403);
    }
}
