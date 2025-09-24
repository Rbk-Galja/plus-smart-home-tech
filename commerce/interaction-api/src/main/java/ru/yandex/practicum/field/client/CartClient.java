package ru.yandex.practicum.field.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.cart.ChangeProductQuantityRequest;
import ru.yandex.practicum.cart.ShoppingCartDto;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@FeignClient(name = "SHOPPING-CART")
public interface CartClient {

    @GetMapping("/api/v1/shopping-cart")
    ShoppingCartDto getCart(@RequestParam("username") String username);

    @PutMapping("/api/v1/shopping-cart")
    ShoppingCartDto addProduct(
            @RequestParam("username") String username,
            @RequestBody Map<UUID, Long> newProduct
    );

    @DeleteMapping("/api/v1/shopping-cart")
    void deleteCart(@RequestParam("username") String username);

    @PostMapping("/api/v1/shopping-cart/remove")
    ShoppingCartDto deleteProductFromCart(
            @RequestParam("username") String username,
            @RequestBody List<UUID> productIds
    );

    @PostMapping("/api/v1/shopping-cart/change-quantity")
    ShoppingCartDto changeProductQuantity(
            @RequestParam("username") String username,
            @RequestBody ChangeProductQuantityRequest productQuantityRequest
    );
}
