package ru.yandex.practicum.order;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.cart.ShoppingCartDto;
import ru.yandex.practicum.warehouse.AddressDto;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateNewOrderRequest {
    @NotNull(message = "Не указана корзина для заказа")
    ShoppingCartDto shoppingCart;
    @NotNull(message = "Не указан адрес доставки")
    AddressDto deliveryAddress;
}
