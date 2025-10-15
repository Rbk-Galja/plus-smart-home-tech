package ru.yandex.practicum;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import ru.yandex.practicum.feign.client.CartClient;
import ru.yandex.practicum.feign.client.DeliveryClient;
import ru.yandex.practicum.feign.client.PaymentClient;
import ru.yandex.practicum.feign.client.WarehouseClient;

@SpringBootApplication
@EnableFeignClients(clients = {CartClient.class, WarehouseClient.class, DeliveryClient.class, PaymentClient.class})
public class Order {
    public static void main(String[] args) {
        SpringApplication.run(Order.class, args);
    }
}