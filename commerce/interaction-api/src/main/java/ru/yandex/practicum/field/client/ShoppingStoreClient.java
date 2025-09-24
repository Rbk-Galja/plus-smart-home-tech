package ru.yandex.practicum.field.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.product.ProductCategory;
import ru.yandex.practicum.product.ProductDto;
import ru.yandex.practicum.product.QuantityState;

import java.util.UUID;

@FeignClient(name = "SHOPPING-STORE")
public interface ShoppingStoreClient {

    @GetMapping("/api/v1/shopping-store")
    Page<ProductDto> getProducts(
            @RequestParam("category") ProductCategory category,
            @SpringQueryMap Pageable pageable
    );

    @PutMapping("/api/v1/shopping-store")
    ProductDto createProduct(@RequestBody ProductDto productDto);

    @PostMapping("/api/v1/shopping-store")
    ProductDto updateProduct(@RequestBody ProductDto productDto);

    @PostMapping("/api/v1/shopping-store/removeProductFromStore")
    boolean removeProduct(@RequestBody UUID productId);

    @PostMapping("/api/v1/shopping-store/quantityState")
    boolean changeQuantityState(
            @RequestParam("productId") UUID productId,
            @RequestParam("quantityState") QuantityState quantityState
    );

    @GetMapping("/api/v1/shopping-store/{productId}")
    ProductDto getProductById(@PathVariable("productId") UUID productId);
}
