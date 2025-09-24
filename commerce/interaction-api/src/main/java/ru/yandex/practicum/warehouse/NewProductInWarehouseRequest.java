package ru.yandex.practicum.warehouse;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NewProductInWarehouseRequest {
    @NotNull(message = "Не указан индетификатор товара")
    UUID productId;
    Boolean fragile = false;
    @NotNull(message = "Не указаны размеры товара")
    DimensionDto dimension;
    @NotNull(message = "Не указан вес товара")
    Double weight;
}
