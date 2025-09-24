package ru.yandex.practicum.model;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.List;

public class ProductNotFoundException extends RuntimeException {
    private final HttpStatus httpStatus;
    @Getter
    private final String userMessage;

    public ProductNotFoundException(String message, String userMessage) {
        super(message);
        this.httpStatus = HttpStatus.NOT_FOUND;
        this.userMessage = userMessage;
    }

    public ProductNotFoundException(String message, String userMessage, Throwable cause) {
        super(message, cause);
        this.httpStatus = HttpStatus.NOT_FOUND;
        this.userMessage = userMessage;
    }

    public String getHttpStatus() {
        return httpStatus.value() + " " + httpStatus.name();
    }

    public List<StackTraceElement> getFullStackTrace() {
        return Arrays.asList(this.getStackTrace());
    }
}
