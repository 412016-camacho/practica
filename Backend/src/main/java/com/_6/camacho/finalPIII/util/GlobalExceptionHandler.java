package com._6.camacho.finalPIII.util;

import com._6.camacho.finalPIII.dtos.common.ErrorApi;
import com._6.camacho.finalPIII.dtos.common.ErrorDetailDto;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

import java.sql.Timestamp;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorApi> handleError(Exception ex, HttpServletRequest request) {
        ErrorApi error = buildError(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, request.getRequestURI(), null);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorApi> handleError(MethodArgumentNotValidException ex, HttpServletRequest request) {
        List<ErrorDetailDto> details = ex.getBindingResult().getFieldErrors().stream()
                .map(this::mapFieldError)
                .collect(Collectors.toList());

        ErrorApi error = buildError("Error de validación", HttpStatus.BAD_REQUEST, request.getRequestURI(), details);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorApi> handleError(ResponseStatusException ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.valueOf(ex.getStatusCode().value());
        ErrorApi error = buildError(ex.getReason(), status, request.getRequestURI(), null);
        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorApi> handleError(EntityNotFoundException ex, HttpServletRequest request) {
        ErrorApi error = buildError(ex.getMessage(), HttpStatus.NOT_FOUND, request.getRequestURI(), null);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorApi> handleError(IllegalArgumentException ex, HttpServletRequest request) {
        ErrorApi error = buildError(ex.getMessage(), HttpStatus.BAD_REQUEST, request.getRequestURI(), null);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    private ErrorApi buildError(String message, HttpStatus status, String path, List<ErrorDetailDto> details) {
        return ErrorApi.builder()
                .timeStamp(String.valueOf(Timestamp.from(ZonedDateTime.now().toInstant())))
                .error(status.getReasonPhrase())
                .status(status.value())
                .message(message)
                .path(path)
                .details(details)
                .build();
    }

    private ErrorDetailDto mapFieldError(FieldError fieldError) {
        return ErrorDetailDto.builder()
                .field(fieldError.getField())
                .issue(fieldError.getDefaultMessage())
                .build();
    }
}
