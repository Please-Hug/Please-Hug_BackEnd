package org.example.hugmeexp.global.exception;

import org.example.hugmeexp.domain.mission.exception.MissionGroupNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MissionGroupNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(MissionGroupNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ErrorResponse.create(ex, HttpStatus.NOT_FOUND, "미션 그룹을 찾을 수 없습니다."));
    }
}