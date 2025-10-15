package ru.yandex.practicum.feign.client;

import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.cart.ShoppingCartDto;
import ru.yandex.practicum.warehouse.*;

import java.util.Map;
import java.util.UUID;

@FeignClient(name = "WAREHOUSE", path = "/api/v1/warehouse")
public interface WarehouseClient {

    @PutMapping
    void createProduct(@RequestBody NewProductInWarehouseRequest request);

    @PostMapping("/check")
    BookedProductsDto checkProductState(@RequestBody ShoppingCartDto cartDto);

    @PostMapping("/add")
    void addQuantityProductToWarehouse(@RequestBody AddProductToWarehouseRequest request);

    @GetMapping("/address")
    AddressDto getCurrentWarehouseAddress();

    @PostMapping("/return")
    void returnedProduct(@RequestBody Map<UUID, Long> returnedProducts);

    @PostMapping("/assembly")
    BookedProductsDto assemblyProductsForOrder(@RequestBody AssemblyProductsForOrderRequest request);

    @PostMapping("/shipped")
    void shippedToDelivery(ShippedToDeliveryRequest deliveryRequest);
}
