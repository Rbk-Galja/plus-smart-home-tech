package ru.yandex.practicum.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.cart.ShoppingCartDto;
import ru.yandex.practicum.feign.client.WarehouseClient;
import ru.yandex.practicum.service.WarehouseService;
import ru.yandex.practicum.warehouse.AddProductToWarehouseRequest;
import ru.yandex.practicum.warehouse.AddressDto;
import ru.yandex.practicum.warehouse.BookedProductsDto;
import ru.yandex.practicum.warehouse.NewProductInWarehouseRequest;

@RestController
@RequestMapping("/api/v1/warehouse")
@RequiredArgsConstructor
public class WarehouseController implements WarehouseClient {
    private final WarehouseService service;

    @Override
    @PutMapping
    public void createProduct(@RequestBody @Valid NewProductInWarehouseRequest request) {
        service.createProduct(request);
    }

    @Override
    @PostMapping("/check")
    public BookedProductsDto checkProductState(@RequestBody @Valid ShoppingCartDto cartDto) {
        return service.checkProductState(cartDto);
    }

    @Override
    @PostMapping("/add")
    public void addQuantityProductToWarehouse(@RequestBody AddProductToWarehouseRequest request) {
        service.addQuantityProductToWarehouse(request);
    }

    @Override
    @GetMapping("/address")
    public AddressDto getCurrentWarehouseAddress() {
        return service.getCurrentWarehouseAddress();
    }
}
