package ru.yandex.practicum.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(NotAuthorizedUserException.class)
    public ResponseEntity<?> handleNotAuthorized(NotAuthorizedUserException ex) {
        return ResponseEntity.status(401).body(ex);
    }

    @ExceptionHandler(NoProductsInShoppingCartException.class)
    public ResponseEntity<?> handleNoProducts(NoProductsInShoppingCartException ex) {
        return ResponseEntity.badRequest().body(ex);
    }
}
