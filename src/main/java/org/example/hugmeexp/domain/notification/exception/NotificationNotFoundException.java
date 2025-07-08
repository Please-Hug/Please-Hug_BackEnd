package org.example.hugmeexp.domain.notification.exception;

import org.example.hugmeexp.global.common.exception.BaseCustomException;
import org.springframework.http.HttpStatus;

public class NotificationNotFoundException extends BaseCustomException {
    public NotificationNotFoundException(){
        super(HttpStatus.NOT_FOUND,"알림을 찾을 수 없습니다",404);
    }
}
