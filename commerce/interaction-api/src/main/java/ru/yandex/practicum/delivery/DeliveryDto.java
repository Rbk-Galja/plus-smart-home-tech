package ru.yandex.practicum.delivery;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.warehouse.AddressDto;

import java.util.UUID;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DeliveryDto {
    @NotNull
    UUID deliveryId;
    @NotNull
    AddressDto fromAddress;
    @NotNull
    AddressDto toAddress;
    @NotNull
    UUID orderId;
    @NotNull
    DeliveryStateDto deliveryState;
}
