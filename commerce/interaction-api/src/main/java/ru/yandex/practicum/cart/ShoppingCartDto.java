package ru.yandex.practicum.cart;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@ToString
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ShoppingCartDto {
    UUID shoppingCartId;
    Map<UUID, Long> products;

    public ShoppingCartDto(UUID shoppingCartId, Map<UUID, Long> products) {
        this.shoppingCartId = shoppingCartId;
        this.products = products != null ? new HashMap<>(products) : new HashMap<>();
    }

    public Map<UUID, Long> getProducts() {
        return new HashMap<>(products);
    }

    public void mergeProduct(UUID productId, Long quantity) {
        products.merge(productId, quantity, Long::sum);
    }
}
