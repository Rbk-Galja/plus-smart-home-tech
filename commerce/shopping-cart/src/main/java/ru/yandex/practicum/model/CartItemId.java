package ru.yandex.practicum.model;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Embeddable
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CartItemId implements Serializable {
    UUID cartId;
    UUID productId;

    public CartItemId() {}

    public CartItemId(UUID cartId, UUID productId) {
        this.cartId = cartId;
        this.productId = productId;
    }

    public UUID getCartId() { return cartId; }
    public UUID getProductId() { return productId; }

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
