package ru.yandex.practicum.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.feign.client.OrderClient;
import ru.yandex.practicum.order.CreateNewOrderRequest;
import ru.yandex.practicum.order.OrderDto;
import ru.yandex.practicum.order.ProductReturnRequest;
import ru.yandex.practicum.service.OrderService;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/order")
@Validated
public class OrderController implements OrderClient {
    private final OrderService orderService;
    private static final String MESSAGE_ORDER_ID = "id заказа обязателен";

    @GetMapping
    public List<OrderDto> getClientOrders(
            @RequestParam
            @NotBlank(message = "Не указано имя пользователя") String username,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return orderService.getClientOrders(username, page, size);
    }

    @PutMapping
    public OrderDto createNewOrder(@RequestBody @Valid CreateNewOrderRequest request) {
        return orderService.createNewOrder(request);
    }

    @PostMapping("/return")
    public OrderDto productReturn(@RequestBody @Valid ProductReturnRequest request) {
        return orderService.productReturn(request);
    }

    @PostMapping("/payment")
    public OrderDto payment(@RequestBody @NotNull(message = MESSAGE_ORDER_ID) UUID orderId) {
        return orderService.payment(orderId);
    }

    @PostMapping("/payment/failed")
    public OrderDto paymentFailed(@RequestBody @NotNull(message = MESSAGE_ORDER_ID) UUID orderId) {
        return orderService.paymentFailed(orderId);
    }

    @PostMapping("/delivery")
    public OrderDto delivery(@RequestBody @NotNull(message = MESSAGE_ORDER_ID) UUID orderId) {
        return orderService.delivery(orderId);
    }

    @PostMapping("/delivery/failed")
    public OrderDto deliveryFailed(@RequestBody @NotNull(message = MESSAGE_ORDER_ID) UUID orderId) {
        return orderService.deliveryFailed(orderId);
    }

    @PostMapping("/completed")
    public OrderDto complete(@RequestBody @NotNull(message = MESSAGE_ORDER_ID) UUID orderId) {
        return orderService.complete(orderId);
    }

    @PostMapping("/calculate/total")
    public OrderDto calculateTotalCost(@RequestBody @NotNull(message = MESSAGE_ORDER_ID) UUID orderId) {
        return orderService.calculateTotalCost(orderId);
    }

    @PostMapping("/calculate/delivery")
    public OrderDto calculateDeliveryCost(@RequestBody @NotNull(message = MESSAGE_ORDER_ID) UUID orderId) {
        return orderService.calculateDeliveryCost(orderId);
    }

    @PostMapping("/assembly")
    public OrderDto assembly(@RequestBody @NotNull(message = MESSAGE_ORDER_ID) UUID orderId) {
        return orderService.assembly(orderId);
    }

    @PostMapping("/assembly/failed")
    public OrderDto assemblyFailed(@RequestBody @NotNull(message = MESSAGE_ORDER_ID) UUID orderId) {
        return orderService.assemblyFailed(orderId);
    }
}
