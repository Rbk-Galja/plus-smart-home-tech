package ru.yandex.practicum.service;

import jakarta.validation.Valid;
import ru.yandex.practicum.cart.ShoppingCartDto;
import ru.yandex.practicum.warehouse.*;

import java.util.Map;
import java.util.UUID;

public interface WarehouseService {
    void createProduct(@Valid NewProductInWarehouseRequest request);

    BookedProductsDto checkProductState(@Valid ShoppingCartDto cartDto);

    void addQuantityProductToWarehouse(AddProductToWarehouseRequest request);

    AddressDto getCurrentWarehouseAddress();

    void returnedProduct(Map<UUID, Long> returnedProducts);

    BookedProductsDto assemblyProductsForOrder(AssemblyProductsForOrderRequest request);

    void shippedToDelivery(ShippedToDeliveryRequest deliveryRequest);
}
