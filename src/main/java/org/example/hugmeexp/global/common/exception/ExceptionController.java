package org.example.hugmeexp.global.common.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.hugmeexp.global.common.exception.response.ErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.http.HttpStatus;

@Slf4j
@RequiredArgsConstructor
@RestControllerAdvice
public class ExceptionController {


    //BaseCustomException 상속한 따로 정의한 예외처리
    @ExceptionHandler(BaseCustomException.class)
    public ResponseEntity<ErrorResponse> handleCustomException(BaseCustomException exception){
        log.warn("Custom Exception : {} | {} | {}\n", exception.getHttpStatus(), exception.getMessage(), exception.getClass().getSimpleName());

        return ResponseEntity.status(exception.getHttpStatus()).body(
                ErrorResponse.builder()
                    .code(exception.getCode())
                    .message(exception.getMessage())
                    .build());
    }

    //Validation 검증 실패 예외 처리
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleInputFieldException(MethodArgumentNotValidException exception){
        //첫번째 Validation 검증 실패의 오류를 가져옴
        FieldError mainError = exception.getFieldErrors().get(0);
        String field = mainError.getField();
        String message = mainError.getDefaultMessage();

        String responseMessage = field + message;
        log.warn("Validation Exception : {}\n", responseMessage);

        return ResponseEntity.badRequest().body(
                ErrorResponse.builder()
                    .code(400)
                    .message(responseMessage)
                    .build());
    }

    //ContentType(JSON 구조)에 대한 예외 처리
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleJsonException(HttpMessageNotReadableException exception){
        log.warn("ContentType Exception Exception : {}\n", exception.getMessage());

        return ResponseEntity.badRequest()
                .body(ErrorResponse.builder()
                        .code(400)
                        .message("ContentType 값이 올바르지 않습니다.")
                        .build());
    }

    //지원하지 않는 HTTP method를 사용할 시 예외처리
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleRequestMethodException(HttpRequestMethodNotSupportedException exception){
        log.warn("Http Method not supported Exception : {}\n", exception.getMessage());

        return ResponseEntity.badRequest()
                .body(ErrorResponse.builder()
                        .code(400)
                        .message("지원하지 않는 HTTP Method 입니다.")
                        .build());
    }

    //RequestParam이 누락됐을 시 예외처리
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingRequestParamException(MissingServletRequestParameterException exception){
        log.warn("Missing Servlet RequestParam Exception : {}\n", exception.getMessage());

        return ResponseEntity.badRequest()
                .body(ErrorResponse.builder()
                        .code(400)
                        .message("RequestParam이 누락되었습니다.")
                        .build());
    }

    // 404 에러 처리 추가
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoHandlerFoundException(NoHandlerFoundException exception) {
        log.warn("No Handler Found Exception: {}\n", exception.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ErrorResponse.builder()
                        .code(404)
                        .message("요청한 리소스를 찾을 수 없습니다.")
                        .build());
    }

    //위에서 처리되지 않은 예외를 최종적으로 처리
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> unhandledException(Exception exception, HttpServletRequest request) {
        log.error("UnhandledException: {} {} errMessage={}\n",
                request.getMethod(),
                request.getRequestURI(),
                exception.getMessage());

        return ResponseEntity.internalServerError()
                .body(ErrorResponse.builder()
                        .code(500)
                        .message("일시적으로 접속이 원활하지 않습니다.")
                        .build());
    }

}
