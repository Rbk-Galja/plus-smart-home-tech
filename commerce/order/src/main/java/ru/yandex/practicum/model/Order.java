package ru.yandex.practicum.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.order.OrderStateDto;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "orders")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Order {
    @Id
    @GeneratedValue
    UUID orderId;
    UUID shoppingCartId;
    Map<UUID, Long> products;
    UUID paymentId;
    UUID deliveryId;
    @Enumerated(EnumType.STRING)
    OrderStateDto orderStateDto;
    Double deliveryWeight;
    Double deliveryVolume;
    Boolean fragile;
    BigDecimal totalPrice;
    BigDecimal deliveryPrice;
    BigDecimal productPrice;
}
