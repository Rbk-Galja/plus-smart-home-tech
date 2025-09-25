package ru.yandex.practicum.cart;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ShoppingCartDto {
    UUID shoppingCartId;
    Map<UUID, Long> products;
}
