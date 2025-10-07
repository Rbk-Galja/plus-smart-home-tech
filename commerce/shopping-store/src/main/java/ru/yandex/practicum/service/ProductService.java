package ru.yandex.practicum.service;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.yandex.practicum.product.ProductCategory;
import ru.yandex.practicum.product.ProductDto;
import ru.yandex.practicum.product.SetProductQuantityStateRequest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface ProductService {
    Page<ProductDto> getProductPage(ProductCategory category, Pageable pageable);

    ProductDto createProduct(@Valid ProductDto productDto);

    ProductDto updateProduct(@Valid ProductDto productDto);

    boolean removeProduct(UUID productId);

    boolean changeQuantityState(@Valid SetProductQuantityStateRequest productDto);

    ProductDto getProductById(UUID productId);

    Map<UUID, BigDecimal> getProductPrices(List<UUID> productIds);
}
