package ru.yandex.practicum.exception;

import lombok.Getter;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.List;

@Getter
public class SpecifiedProductAlreadyInWarehouseException extends DataIntegrityViolationException {
    private final String userMessage = "Данный товар уже есть на складе";

    public SpecifiedProductAlreadyInWarehouseException(String message) {
        super(message);
    }

    public List<StackTraceElement> getFullStackTrace() {
        return Arrays.asList(this.getStackTrace());
    }
}
