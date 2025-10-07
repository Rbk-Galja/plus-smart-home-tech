package ru.yandex.practicum.service;

import ru.yandex.practicum.delivery.DeliveryDto;
import ru.yandex.practicum.order.OrderDto;

import java.math.BigDecimal;
import java.util.UUID;

public interface DeliveryService {
    DeliveryDto createOrder(DeliveryDto deliveryDto);

    void successfulDelivery(UUID deliveryId);

    void pickedDelivery(UUID deliveryId);

    void failedDelivery(UUID deliveryId);

    BigDecimal costDelivery(OrderDto orderDto);
}
