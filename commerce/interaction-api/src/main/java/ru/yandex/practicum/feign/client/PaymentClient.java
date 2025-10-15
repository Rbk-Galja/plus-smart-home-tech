package ru.yandex.practicum.feign.client;

import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.order.OrderDto;
import ru.yandex.practicum.payment.PaymentDto;

import java.math.BigDecimal;
import java.util.UUID;

@FeignClient(name = "PAYMENT", path = "/api/v1/payment")
public interface PaymentClient {
    @PostMapping
    PaymentDto createPayment(@RequestBody OrderDto orderDto);

    @PostMapping("/totalCost")
    BigDecimal totalCost(@RequestBody OrderDto orderDto);

    @PostMapping("/refund")
    void refundOrder(@RequestBody UUID paymentId);

    @PostMapping("/productCost")
    BigDecimal calculateOrderTotal(@RequestBody @Valid OrderDto orderDto);

    @PostMapping("/failed")
    void failedPayment(@RequestBody UUID paymentId);
}
