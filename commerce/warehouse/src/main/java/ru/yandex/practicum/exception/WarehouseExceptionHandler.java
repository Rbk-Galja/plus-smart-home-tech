package ru.yandex.practicum.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class WarehouseExceptionHandler {
    @ExceptionHandler(SpecifiedProductAlreadyInWarehouseException.class)
    public ResponseEntity<Map<String, Object>> handleAlreadyExists(
            SpecifiedProductAlreadyInWarehouseException ex) {

        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("message", ex.getMessage());
        errorDetails.put("userMessage", ex.getUserMessage());
        errorDetails.put("stackTrace", ex.getFullStackTrace());

        return ResponseEntity.badRequest()
                .body(errorDetails);
    }

    @ExceptionHandler(ProductInShoppingCartLowQuantityInWarehouse.class)
    public ResponseEntity<?> handleLowQuantity(ProductInShoppingCartLowQuantityInWarehouse ex) {
        return ResponseEntity.badRequest().body(ex);
    }

    @ExceptionHandler(NoSpecifiedProductInWarehouseException.class)
    public ResponseEntity<?> handleNoProduct(NoSpecifiedProductInWarehouseException ex) {
        return ResponseEntity.badRequest().body(ex);
    }
}
