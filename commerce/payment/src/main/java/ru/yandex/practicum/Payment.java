package ru.yandex.practicum;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import ru.yandex.practicum.feign.client.OrderClient;
import ru.yandex.practicum.feign.client.ShoppingStoreClient;

@SpringBootApplication
@EnableFeignClients(clients = {ShoppingStoreClient.class, OrderClient.class})
public class Payment {
    public static void main(String[] args) {
        SpringApplication.run(Payment.class, args);
    }
}