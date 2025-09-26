package ru.yandex.practicum.feign.client;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.cart.ChangeProductQuantityRequest;
import ru.yandex.practicum.cart.ShoppingCartDto;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@FeignClient(name = "SHOPPING-CART", path = "/api/v1/shopping-cart")
public interface CartClient {

    @GetMapping
    ShoppingCartDto getCart(@RequestParam("username") @NotBlank(message = "Имя пользователя на должно быть пустым") String username);

    @PutMapping
    ShoppingCartDto addProduct(
            @RequestParam("username") @NotBlank(message = "Имя пользователя на должно быть пустым") String username,
            @RequestBody @NotEmpty Map<UUID, Long> newProduct
    );

    @DeleteMapping
    void deleteCart(@RequestParam("username") @NotBlank(message = "Имя пользователя на должно быть пустым") String username);

    @PostMapping("/remove")
    ShoppingCartDto deleteProductFromCart(
            @RequestParam("username") @NotBlank(message = "Имя пользователя на должно быть пустым") String username,
            @RequestBody @NotEmpty List<UUID> productIds
    );

    @PostMapping("/change-quantity")
    ShoppingCartDto changeProductQuantity(
            @RequestParam("username") @NotBlank(message = "Имя пользователя на должно быть пустым") String username,
            @RequestBody ChangeProductQuantityRequest productQuantityRequest
    );
}
