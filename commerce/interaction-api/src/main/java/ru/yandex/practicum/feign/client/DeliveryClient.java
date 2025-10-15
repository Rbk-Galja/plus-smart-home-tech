package ru.yandex.practicum.feign.client;

import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.delivery.DeliveryDto;
import ru.yandex.practicum.order.OrderDto;

import java.math.BigDecimal;
import java.util.UUID;

@FeignClient(name = "DELIVERY", path = "/api/v1/delivery")
public interface DeliveryClient {
    @PutMapping
    DeliveryDto createOrder(@RequestBody DeliveryDto deliveryDto);

    @PostMapping("/successful")
    void successfulDelivery(@RequestBody UUID deliveryId);

    @PostMapping("/picked")
    void pickedDelivery(@RequestBody UUID deliveryId);

    @PostMapping("/failed")
    void failedDelivery(@RequestBody UUID deliveryId);

    @PostMapping("/cost")
    BigDecimal costDelivery(@Valid @RequestBody OrderDto orderDto);
}
