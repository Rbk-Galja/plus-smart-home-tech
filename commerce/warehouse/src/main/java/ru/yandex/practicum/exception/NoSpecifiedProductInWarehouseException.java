package ru.yandex.practicum.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class NoSpecifiedProductInWarehouseException extends RuntimeException {
    private final String httpStatus = HttpStatus.BAD_REQUEST.toString();
    private final String userMessage;

    public NoSpecifiedProductInWarehouseException(String message, String userMessage) {
        super(message);
        this.userMessage = userMessage;
    }
}
