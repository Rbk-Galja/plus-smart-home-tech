package ru.yandex.practicum.exception;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(NotAuthorizedUserException.class)
    public ResponseEntity<?> handleNotAuthorized(NotAuthorizedUserException ex) {
        log.error("Попытка доступа не авторизованного пользователя {}", ex.getMessage());
        return ResponseEntity.status(401).body(ex);
    }

    @ExceptionHandler(NoProductsInShoppingCartException.class)
    public ResponseEntity<?> handleNoProducts(NoProductsInShoppingCartException ex) {
        log.error("Запрашиваемого товара нет в корзине {}", ex.getMessage());
        return ResponseEntity.badRequest().body(ex);
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
}
