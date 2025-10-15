package ru.yandex.practicum.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.delivery.DeliveryDto;
import ru.yandex.practicum.feign.client.DeliveryClient;
import ru.yandex.practicum.order.OrderDto;
import ru.yandex.practicum.service.DeliveryService;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/delivery")
@RequiredArgsConstructor
public class DeliveryController implements DeliveryClient {
    private final DeliveryService service;

    @Override
    @PutMapping
    public DeliveryDto createOrder(@Valid @RequestBody DeliveryDto deliveryDto) {
        return service.createOrder(deliveryDto);
    }

    @Override
    @PostMapping("/successful")
    public void successfulDelivery(@RequestBody UUID deliveryId) {
        service.successfulDelivery(deliveryId);
    }

    @Override
    @PostMapping("/picked")
    public void pickedDelivery(@RequestBody UUID deliveryId) {
        service.pickedDelivery(deliveryId);
    }

    @Override
    @PostMapping("/failed")
    public void failedDelivery(@RequestBody UUID deliveryId) {
        service.failedDelivery(deliveryId);
    }

    @Override
    @PostMapping("/cost")
    public BigDecimal costDelivery(@Valid @RequestBody OrderDto orderDto) {
        return service.costDelivery(orderDto);
    }
}
