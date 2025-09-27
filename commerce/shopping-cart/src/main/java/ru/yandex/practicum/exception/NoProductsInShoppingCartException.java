package ru.yandex.practicum.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

public class NoProductsInShoppingCartException extends RuntimeException {
    private final HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
    @Getter
    private final String userMessage;

    public NoProductsInShoppingCartException(String message, String userMessage) {
        super(message);
        this.userMessage = userMessage;
    }

    public String getHttpStatus() {
        return httpStatus.value() + " " + httpStatus.name();
    }
}
