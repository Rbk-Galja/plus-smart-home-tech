package ru.yandex.practicum.product;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductDto {
    UUID productId;
    @NotEmpty(message = "Указано пустое название продукта")
    String productName;
    @NotEmpty(message = "Указано пустое описание продукта")
    String description;
    String imageSrc;
    @NotNull(message = "Не указан остаток товара")
    QuantityState quantityState;
    @NotNull(message = "Указан пустой статус продукта")
    ProductState productState;
    ProductCategory productCategory;
    @NotNull(message = "Указана пустая цена продукта")
    @DecimalMin(value = "1.00", inclusive = true, message = "Цена не может быть меньше 1")
    BigDecimal price;
}
