package ru.yandex.practicum.model;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.util.Objects;
import java.util.UUID;

@Embeddable
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CartItemId {
    UUID cartId;
    @Getter
    UUID productId;

    public CartItemId() {
    }

    public CartItemId(UUID cartId, UUID productId) {
        this.cartId = cartId;
        this.productId = productId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CartItemId)) return false;
        CartItemId that = (CartItemId) o;
        return Objects.equals(cartId, that.cartId) &&
                Objects.equals(productId, that.productId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cartId, productId);
    }
}
