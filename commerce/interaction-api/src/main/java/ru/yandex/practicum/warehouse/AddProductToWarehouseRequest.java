package ru.yandex.practicum.warehouse;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AddProductToWarehouseRequest {
    @NotNull(message = "Не указан индитификатор добавляемого товара")
    UUID productId;
    @NotNull(message = "Не указано количество добавляемого товара")
    @DecimalMin(value = "1", message = "Количество добавляемого товара не может быть меньше 1")
    Long quantity;

}
