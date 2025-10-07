package ru.yandex.practicum.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

public class CartDeactivatedException extends RuntimeException {
    private final HttpStatus httpStatus = HttpStatus.UNAUTHORIZED;
    @Getter
    private final String userMessage;

    public CartDeactivatedException(String message, String userMessage) {
        super(message);
        this.userMessage = userMessage;
    }

    public String getHttpStatus() {
        return httpStatus.value() + " " + httpStatus.name();
    }
}
