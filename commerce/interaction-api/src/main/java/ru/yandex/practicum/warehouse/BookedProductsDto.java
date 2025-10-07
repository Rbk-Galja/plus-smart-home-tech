package ru.yandex.practicum.warehouse;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookedProductsDto {
    @NotNull(message = "Не указан вес для забронированного товара")
    Double deliveryWeight;
    @NotNull(message = "Не указан объем забронированного товара")
    Double deliveryVolume;
    @NotNull(message = "Не указано наличие хрупких товаров")
    Boolean fragile;
}
