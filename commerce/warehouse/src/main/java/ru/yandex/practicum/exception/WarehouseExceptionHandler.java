package ru.yandex.practicum.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class WarehouseExceptionHandler {
    @ExceptionHandler(SpecifiedProductAlreadyInWarehouseException.class)
    public ResponseEntity<?> handleAlreadyExists(SpecifiedProductAlreadyInWarehouseException ex) {
        return ResponseEntity.badRequest().body(ex);
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
