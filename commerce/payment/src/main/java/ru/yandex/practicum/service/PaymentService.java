package ru.yandex.practicum.service;

import ru.yandex.practicum.order.OrderDto;
import ru.yandex.practicum.payment.PaymentDto;

import java.math.BigDecimal;
import java.util.UUID;

public interface PaymentService {
    PaymentDto createPayment(OrderDto orderDto);

    BigDecimal totalCost(OrderDto orderDto);

    void refundOrder(UUID paymentId);

    BigDecimal calculateOrderTotal(OrderDto orderDto);

    void failedPayment(UUID paymentId);
}
