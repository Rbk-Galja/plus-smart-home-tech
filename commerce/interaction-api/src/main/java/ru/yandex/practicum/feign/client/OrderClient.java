package ru.yandex.practicum.feign.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.order.CreateNewOrderRequest;
import ru.yandex.practicum.order.OrderDto;
import ru.yandex.practicum.order.ProductReturnRequest;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "ORDER", path = "/api/v1/order")
public interface OrderClient {
    @GetMapping
    List<OrderDto> getClientOrders(
            @RequestParam String username,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size);

    @PutMapping
    OrderDto createNewOrder(@RequestBody CreateNewOrderRequest request);

    @PostMapping("/return")
    OrderDto productReturn(@RequestBody ProductReturnRequest request);

    OrderDto payment(@RequestBody UUID orderId);

    @PostMapping("/payment/failed")
    OrderDto paymentFailed(@RequestBody  UUID orderId);

    @PostMapping("/delivery")
    OrderDto delivery(@RequestBody UUID orderId);

    @PostMapping("/delivery/failed")
    OrderDto deliveryFailed(@RequestBody UUID orderId);

    @PostMapping("/completed")
    OrderDto complete(@RequestBody  UUID orderId);

    @PostMapping("/calculate/total")
    OrderDto calculateTotalCost(@RequestBody UUID orderId);

    @PostMapping("/calculate/delivery")
    OrderDto calculateDeliveryCost(@RequestBody UUID orderId);

    @PostMapping("/assembly")
    OrderDto assembly(@RequestBody  UUID orderId);

    @PostMapping("/assembly/failed")
    OrderDto assemblyFailed(@RequestBody UUID orderId);
}
