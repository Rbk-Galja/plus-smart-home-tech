package ru.yandex.practicum.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "shopping_cart_products")
@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CartItem {
    @EmbeddedId
    CartItemId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("cartId")
    Cart cart;

    @Column(nullable = false)
    Long quantity;
}
