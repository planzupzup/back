package travel.travel.common;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import travel.travel.common.dto.CommonErrorDto;
import travel.travel.common.exception.CustomException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<CommonErrorDto> SecurityExceptionHandler(SecurityException e) {
        e.printStackTrace();
        return new ResponseEntity<>(CommonErrorDto.of(HttpStatus.FORBIDDEN, e.getMessage()), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<CommonErrorDto> CustomExceptionHandler(CustomException e) {
        e.printStackTrace();
        return new ResponseEntity<>(CommonErrorDto.of(e.getErrorCode().getHttpStatus(), e.getErrorCode().getMessage()), e.getErrorCode().getHttpStatus());
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<CommonErrorDto> runtimeExceptionHandler (RuntimeException e) {
        e.printStackTrace();
        return new ResponseEntity<>(CommonErrorDto.of(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage()),HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<CommonErrorDto> exceptionHandler (Exception e) {
        e.printStackTrace();
        return new ResponseEntity<>(CommonErrorDto.of(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage()),HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
