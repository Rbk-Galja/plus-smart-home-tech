package ru.yandex.practicum.controller;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.cart.ChangeProductQuantityRequest;
import ru.yandex.practicum.cart.ShoppingCartDto;
import ru.yandex.practicum.feign.client.CartClient;
import ru.yandex.practicum.service.CartService;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/shopping-cart")
@RequiredArgsConstructor
@Validated
public class CartController implements CartClient {
    private final CartService cartService;

    @Override
    @GetMapping
    public ShoppingCartDto getCart(@RequestParam @NotBlank(message = "Имя пользователя на должно быть пустым") String username) {
        return cartService.getCart(username);
    }

    @Override
    @PutMapping
    public ShoppingCartDto addProduct(@RequestParam @NotBlank(message = "Имя пользователя на должно быть пустым") String username,
                                      @RequestBody @NotEmpty Map<UUID, Long> newProduct) {
        return cartService.addProductsInCart(username, newProduct);
    }

    @Override
    @DeleteMapping
    public void deleteCart(@RequestParam @NotBlank(message = "Имя пользователя на должно быть пустым") String username) {
        cartService.deleteCart(username);
    }

    @Override
    @PostMapping("/remove")
    public ShoppingCartDto deleteProductFromCart(@RequestParam @NotBlank(message = "Имя пользователя на должно быть пустым") String username,
                                                 @RequestBody @NotEmpty List<UUID> productIds) {
        return cartService.deleteProductFromCart(username, productIds);
    }

    @Override
    @PostMapping("/change-quantity")
    public ShoppingCartDto changeProductQuantity(@RequestParam @NotBlank(message = "Имя пользователя на должно быть пустым") String username,
                                                 @RequestBody ChangeProductQuantityRequest productQuantityRequest) {
        return cartService.changeProductQuantity(username, productQuantityRequest);
    }
}
