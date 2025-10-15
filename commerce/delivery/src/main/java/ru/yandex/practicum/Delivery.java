package ru.yandex.practicum;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import ru.yandex.practicum.feign.client.OrderClient;
import ru.yandex.practicum.feign.client.WarehouseClient;

@SpringBootApplication
@EnableFeignClients(clients = {WarehouseClient.class, OrderClient.class})
public class Delivery {
    public static void main(String[] args) {
        SpringApplication.run(Delivery.class, args);
    }
}