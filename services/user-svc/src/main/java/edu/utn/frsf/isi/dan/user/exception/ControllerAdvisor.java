package edu.utn.frsf.isi.dan.user.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;

@RestControllerAdvice
public class ControllerAdvisor {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ExceptionInfo> handleIllegalArgumentException(IllegalArgumentException ex, WebRequest request) {
        ExceptionInfo exceptionInfo = new ExceptionInfo(
                ex.getMessage(),
                request.getDescription(false),
                String.valueOf(System.currentTimeMillis()),
                HttpStatus.BAD_REQUEST.value()
        );
        return new ResponseEntity<>(exceptionInfo, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ExceptionInfo> handleEntityNotFoundException(EntityNotFoundException ex, WebRequest request) {
        ExceptionInfo exceptionInfo = new ExceptionInfo(
                ex.getMessage(),
                request.getDescription(false),
                String.valueOf(System.currentTimeMillis()),
                HttpStatus.NOT_FOUND.value()
        );
        return new ResponseEntity<>(exceptionInfo, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionInfo> handleGeneralException(Exception ex, WebRequest request) {
        ExceptionInfo exceptionInfo = new ExceptionInfo(
                ex.getMessage(),
                request.getDescription(false),
                String.valueOf(System.currentTimeMillis()),
                HttpStatus.INTERNAL_SERVER_ERROR.value()
        );
        return new ResponseEntity<>(exceptionInfo, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionInfo> handleValidationException(MethodArgumentNotValidException ex, WebRequest request) {
        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .reduce((message1, message2) -> message1 + ", " + message2)
                .orElse("Validation error");

        ExceptionInfo exceptionInfo = new ExceptionInfo(
                errorMessage,
                request.getDescription(false),
                String.valueOf(System.currentTimeMillis()),
                HttpStatus.BAD_REQUEST.value()
        );
        return new ResponseEntity<>(exceptionInfo, HttpStatus.BAD_REQUEST);
    }

}
