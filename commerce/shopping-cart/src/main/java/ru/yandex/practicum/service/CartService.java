package ru.yandex.practicum.service;

import ru.yandex.practicum.cart.ChangeProductQuantityRequest;
import ru.yandex.practicum.cart.ShoppingCartDto;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface CartService {
    ShoppingCartDto getCart(String username);

    ShoppingCartDto addProductsInCart(String username, Map<UUID, Long> newProduct);

    void deleteCart(String username);

    void deleteProductFromCart(String username, List<UUID> productIds);

    ShoppingCartDto changeProductQuantity(String username, ChangeProductQuantityRequest request);
}
