package ru.yandex.practicum.warehouse;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DimensionDto {
    @DecimalMin(value = "1.0", message = "Минимально допустимое значение ширины 0.0")
    @NotNull
    Double width;
    @NotNull
    @DecimalMin(value = "1.0", message = "Минимально допустимое значение высоты 0.0")
    Double height;
    @NotNull
    @DecimalMin(value = "1.0", message = "Минимально допустимое значение глубины 0.0")
    Double depth;
}
