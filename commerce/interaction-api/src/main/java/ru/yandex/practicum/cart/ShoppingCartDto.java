package ru.yandex.practicum.cart;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ShoppingCartDto {
    UUID shoppingCartId;
    Map<UUID, Long> products;

    public Map<UUID, Long> getProducts() {
        return new HashMap<>(products);
    }

    public void mergeProduct(UUID productId, Long quantity) {
        products.merge(productId, quantity, Long::sum);
    }
}
