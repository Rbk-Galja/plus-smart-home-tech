package ru.yandex.practicum.exception;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.exception.ExceptionResponse.Cause;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    private ExceptionResponse buildResponse(RuntimeException ex) {
        List<ExceptionResponse.StackTraceElementDto> stackTrace = Arrays.stream(ex.getStackTrace())
                .map(ExceptionResponse.StackTraceElementDto::new)
                .collect(Collectors.toList());

        Cause cause = new Cause(stackTrace, ex.getMessage(), ex.getLocalizedMessage());
        return new ExceptionResponse(cause);
    }

    @ExceptionHandler(NoSpecifiedProductInWarehouseException.class)
    public ResponseEntity<ExceptionResponse> handleNoSpecifiedProduct(NoSpecifiedProductInWarehouseException ex) {
        log.error("Товар не найден на складе {}", ex.getMessage());
        return new ResponseEntity<>(buildResponse(ex), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(NoOrderFoundException.class)
    public ResponseEntity<ExceptionResponse> handleNoOrderFound(NoOrderFoundException ex) {
        log.error("Попытка доступа к несуществующему заказу {}", ex.getMessage());
        return new ResponseEntity<>(buildResponse(ex), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(NotAuthorizedUserException.class)
    public ResponseEntity<ExceptionResponse> handleNotAuthorized(NotAuthorizedUserException ex) {
        log.error("Попытка доступа от неавторизованного пользователя {}", ex.getMessage());
        return new ResponseEntity<>(buildResponse(ex), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        Map<String, Object> body = new HashMap<>();
        body.put("message", "Validation failed");
        body.put("errors", errors);
        body.put("httpStatus", "400 BAD_REQUEST");
        log.error("Ошибка валидации входящих данных API {}", ex.getMessage());
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, List<String>>> handleNotValid(ConstraintViolationException ex) {
        Map<String, List<String>> errors = new HashMap<>();
        ex.getConstraintViolations().forEach(violation -> {
            errors.computeIfAbsent(violation.getPropertyPath().toString(), k -> new ArrayList<>())
                    .add(violation.getMessage());
        });
        log.error("Ошибка валидации данных {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<Map<String, Object>> handleServerError(Throwable ex) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", "Internal Server Error");
        errorResponse.put("message", ex.getMessage());
        errorResponse.put("timestamp", System.currentTimeMillis());
        log.error("Ошибка работы сервера {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}
