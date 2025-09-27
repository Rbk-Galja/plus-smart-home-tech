package ru.yandex.practicum.service;

import jakarta.validation.Valid;
import ru.yandex.practicum.cart.ShoppingCartDto;
import ru.yandex.practicum.warehouse.AddProductToWarehouseRequest;
import ru.yandex.practicum.warehouse.AddressDto;
import ru.yandex.practicum.warehouse.BookedProductsDto;
import ru.yandex.practicum.warehouse.NewProductInWarehouseRequest;

public interface WarehouseService {
    void createProduct(@Valid NewProductInWarehouseRequest request);

    BookedProductsDto checkProductState(@Valid ShoppingCartDto cartDto);

    void addQuantityProductToWarehouse(AddProductToWarehouseRequest request);

    AddressDto getCurrentWarehouseAddress();
}
