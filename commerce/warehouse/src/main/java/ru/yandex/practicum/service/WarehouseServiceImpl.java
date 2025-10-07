package ru.yandex.practicum.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.cart.ShoppingCartDto;
import ru.yandex.practicum.exception.NoSpecifiedProductInWarehouseException;
import ru.yandex.practicum.exception.ProductInShoppingCartLowQuantityInWarehouse;
import ru.yandex.practicum.exception.SpecifiedProductAlreadyInWarehouseException;
import ru.yandex.practicum.feign.client.ShoppingStoreClient;
import ru.yandex.practicum.mapper.WarehouseMapper;
import ru.yandex.practicum.model.Booking;
import ru.yandex.practicum.model.WarehouseProduct;
import ru.yandex.practicum.product.QuantityState;
import ru.yandex.practicum.repository.BookingRepository;
import ru.yandex.practicum.repository.WarehouseProductRepository;
import ru.yandex.practicum.warehouse.*;

import java.security.SecureRandom;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class WarehouseServiceImpl implements WarehouseService {
    private final WarehouseProductRepository repository;
    private final WarehouseMapper mapper;
    private final ShoppingStoreClient shoppingStoreClient;
    private final BookingRepository bookingRepository;

    private static final String[] ADDRESSES = {"ADDRESS_1", "ADDRESS_2"};

    private static final String CURRENT_ADDRESS =
            ADDRESSES[Random.from(new SecureRandom()).nextInt(0, 1)];


    @Override
    @Transactional
    public void createProduct(@Valid NewProductInWarehouseRequest request) {
        log.info("Начинаем добавление нового товара на склад: {}", request);
        repository.findById(request.getProductId())
                .ifPresent(p -> {
                    throw new SpecifiedProductAlreadyInWarehouseException(
                            "Товар уже создан", "Товар с id " + request.getProductId() + " уже создан");
                });

        repository.save(mapper.toEntity(request));
        log.info("Добавление товара на склад прошло успешно");
    }

    @Override
    @Transactional
    public BookedProductsDto checkProductState(@Valid ShoppingCartDto cartDto) {
        log.info("Проверка остатка товара на складе");
        Set<UUID> productIds = cartDto.getProducts().keySet();
        List<WarehouseProduct> products = repository.findAllById(productIds);
        if (products.size() != productIds.size()) {
            log.error("Некоторые товары не найдены на складе");
            throw new ProductInShoppingCartLowQuantityInWarehouse(
                    "Не все товары присутствуют на складе",
                    "Некоторые товары не найдены в базе данных склада"
            );
        }

        double totalWeight = 0;
        double totalVolume = 0;
        boolean hasFragile = false;

        for (WarehouseProduct product : products) {
            long requiredQty = cartDto.getProducts().get(product.getProductId());

            if (product.getQuantity() < requiredQty) {
                log.error("Недостаточно товара на складе");
                throw new ProductInShoppingCartLowQuantityInWarehouse(
                        "Недостаточно товара на складе",
                        "Товара с id " + product.getProductId() + " доступно " + product.getQuantity()
                );
            }

            totalWeight += product.getWeight() * requiredQty;
            totalVolume += product.getWidth() * product.getHeight() * product.getDepth() * requiredQty;

            if (product.isFragile()) {
                hasFragile = true;
            }
        }

        BookedProductsDto dto = new BookedProductsDto();
        dto.setDeliveryWeight(totalWeight);
        dto.setDeliveryVolume(totalVolume);
        dto.setFragile(hasFragile);
        log.info("Проверка остатка товара на складе завершена успешно");
        return dto;
    }

    @Override
    @Transactional
    public void addQuantityProductToWarehouse(AddProductToWarehouseRequest request) {
        log.info("Начинаем прием товара {} на склад", request);
        WarehouseProduct product = repository.findById(request.getProductId())
                .orElseThrow(() -> new NoSpecifiedProductInWarehouseException("Товар не найден",
                        "Товар с Id" + request.getProductId() + " не найден"));
        product.setQuantity(product.getQuantity() + request.getQuantity());
        log.info("Прием товара {} на склад прошел успешно", product);
    }

    @Override
    public AddressDto getCurrentWarehouseAddress() {
        log.info("Начинаем получение адреса склада для рассчета доставки");
        AddressDto addressDto = new AddressDto();
        addressDto.setCountry(CURRENT_ADDRESS);
        addressDto.setCity(CURRENT_ADDRESS);
        addressDto.setStreet(CURRENT_ADDRESS);
        addressDto.setHouse(CURRENT_ADDRESS);
        addressDto.setFlat(CURRENT_ADDRESS);
        log.info("Получение адреса прошло успешно: {}", addressDto);
        return addressDto;
    }

    @Override
    @Transactional
    public void returnedProduct(Map<UUID, Long> returnedProducts) {
        log.info("Начинаем возврат товаров на склад: {}", returnedProducts);
        List<WarehouseProduct> products = repository.findAllById(returnedProducts.keySet());
        for (WarehouseProduct p : products) {
            Long addQty = returnedProducts.get(p.getProductId());
            if (addQty != null && addQty > 0) {
                p.setQuantity(p.getQuantity() + addQty);
                updateProductQuantityInShoppingStore(p);
            }
        }
        log.info("Воврат товаров прошел успешно");
    }

    @Transactional
    public BookedProductsDto assemblyProductsForOrder(AssemblyProductsForOrderRequest request) {
        log.info("Собираем товары для доставки {}", request);
        ShoppingCartDto cartDto = new ShoppingCartDto();
        cartDto.setProducts(request.getProducts());
        cartDto.setShoppingCartId(request.getOrderId());

        BookedProductsDto booked = checkProductState(cartDto);

        for (Map.Entry<UUID, Long> entry : request.getProducts().entrySet()) {
            WarehouseProduct product = repository.findById(entry.getKey())
                    .orElseThrow(() -> new NoSpecifiedProductInWarehouseException(
                            "Товар не найден",
                            "Товар с id " + entry.getKey() + " отсутствует"
                    ));
            product.setQuantity(product.getQuantity() - entry.getValue());
            updateProductQuantityInShoppingStore(product);
        }

        Booking newBooking = Booking.builder()
                .shoppingCartId(request.getOrderId())
                .products(request.getProducts())
                .deliveryWeight(booked.getDeliveryWeight())
                .deliveryVolume(booked.getDeliveryVolume())
                .fragile(booked.getFragile())
                .build();
        bookingRepository.save(newBooking);
        log.info("Товары успешно подготовлены к отправке {}", booked);
        return booked;
    }

    @Override
    public void shippedToDelivery(ShippedToDeliveryRequest deliveryRequest) {
        log.info("Передача товаров в доставку: {}", deliveryRequest);
        Booking booking = bookingRepository.findByOrderId(deliveryRequest.getOrderId()).orElseThrow(
                () -> new NoSpecifiedProductInWarehouseException("Нет информации о товаре на складе.", "Ошибка"));
        booking.setDeliveryId(deliveryRequest.getDeliveryId());
        log.info("Передача товаров в доставку прошла успешно");
    }

    private void updateProductQuantityInShoppingStore(WarehouseProduct product) {
        UUID productId = product.getProductId();
        QuantityState quantityState;
        Long qty = product.getQuantity();
        if (qty == 0) {
            quantityState = QuantityState.ENDED;
        } else if
        (qty < 10) {
            quantityState = QuantityState.ENOUGH;
        } else if (qty < 100) {
            quantityState = QuantityState.FEW;
        } else {
            quantityState = QuantityState.MANY;
        }
        shoppingStoreClient.changeQuantityState(productId, quantityState);
    }
}
