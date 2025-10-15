package ru.yandex.practicum.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.feign.client.PaymentClient;
import ru.yandex.practicum.order.OrderDto;
import ru.yandex.practicum.payment.PaymentDto;
import ru.yandex.practicum.service.PaymentService;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/payment")
@RequiredArgsConstructor
public class PaymentController implements PaymentClient {
    private final PaymentService service;

    @Override
    @PostMapping
    public PaymentDto createPayment(@RequestBody @Valid OrderDto orderDto) {
        return service.createPayment(orderDto);
    }

    @Override
    @PostMapping("/totalCost")
    public BigDecimal totalCost(@RequestBody @Valid OrderDto orderDto) {
        return service.totalCost(orderDto);
    }

    @Override
    @PostMapping("/refund")
    public void refundOrder(@RequestBody UUID paymentId) {
        service.refundOrder(paymentId);
    }

    @Override
    @PostMapping("/productCost")
    public BigDecimal calculateOrderTotal(@RequestBody @Valid OrderDto orderDto) {
        return service.calculateOrderTotal(orderDto);
    }

    @Override
    @PostMapping("/failed")
    public void failedPayment(@RequestBody UUID paymentId) {
        service.failedPayment(paymentId);
    }
}
