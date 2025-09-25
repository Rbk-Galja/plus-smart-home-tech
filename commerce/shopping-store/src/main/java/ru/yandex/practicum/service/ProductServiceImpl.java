package ru.yandex.practicum.service;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.mapper.ProductMapper;
import ru.yandex.practicum.model.Product;
import ru.yandex.practicum.model.ProductNotFoundException;
import ru.yandex.practicum.product.ProductCategory;
import ru.yandex.practicum.product.ProductDto;
import ru.yandex.practicum.product.ProductState;
import ru.yandex.practicum.product.SetProductQuantityStateRequest;
import ru.yandex.practicum.repository.ProductRepository;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository repository;
    private final ProductMapper mapper;

    @Override
    public Page<ProductDto> getProductPage(ProductCategory category, Pageable pageable) {
        log.info("Возвращаем список товаров по запросу: {}", category);
        return repository.findByCategory(category, pageable).map(mapper::toProductDto);
    }

    @Override
    @Transactional
    public ProductDto createProduct(@Valid ProductDto productDto) {
        log.info("Начинаем создание продукта {}", productDto);
        Product product = repository.save(mapper.toProduct(productDto));
        log.info("Создание продукта {} прошло успешно", product);
        return mapper.toProductDto(product);
    }

    @Override
    @Transactional
    public ProductDto updateProduct(@Valid ProductDto productDto) {
        log.info("Началось обновление продукта с id = {}", productDto.getProductId());
        Product product = repository.findById(productDto.getProductId())
                .orElseThrow(() -> new ProductNotFoundException("Продукт с id" + productDto.getProductId() + " не найден",
                        "Product not found", new RuntimeException("Underlying cause")));
        mapper.updateEntityFromDto(productDto, product);
        log.info("Обновление продукта {} прошло успешно", product);
        return mapper.toProductDto(product);
    }

    @Override
    @Transactional
    public boolean removeProduct(UUID productId) {
        log.info("Началось удаление продукта id = {}", productId);
        Product product = repository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Продукт с id" + productId + " не найден",
                        "Product not found", new RuntimeException("Underlying cause")));
        product.setProductState(ProductState.DEACTIVATE);
        log.info("Удаление продукта {} прошло успешно", product);
        return true;
    }

    @Override
    @Transactional
    public boolean changeQuantityState(@Valid SetProductQuantityStateRequest productDto) {
        log.info("Началось обновление остатка товара id = {} на складе", productDto.getProductId());
        Product product = repository.findById(productDto.getProductId())
                .orElseThrow(() -> new ProductNotFoundException("Продукт с id" + productDto.getProductId() + " не найден",
                        "Product not found", new RuntimeException("Underlying cause")));
        product.setQuantityState(productDto.getQuantityState());
        log.info("Обновление остатка товара прошло успешно, статус изменен на {}", product.getQuantityState());
        return true;
    }

    @Override
    public ProductDto getProductById(UUID productId) {
        log.info("Получение товара по id = {}", productId);
        return mapper.toProductDto(repository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Продукт с id" + productId + " не найден",
                        "Product not found", new RuntimeException("Underlying cause"))));
    }
}
