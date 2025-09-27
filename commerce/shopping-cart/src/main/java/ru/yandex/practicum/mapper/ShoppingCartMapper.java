package ru.yandex.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.yandex.practicum.cart.ShoppingCartDto;
import ru.yandex.practicum.model.Cart;
import ru.yandex.practicum.model.CartItem;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING)
public interface ShoppingCartMapper {
    @Mapping(target = "shoppingCartId", source = "cartId")
    @Mapping(target = "products", expression = "java(mapItems(cart))")
    ShoppingCartDto toDto(Cart cart);

    default Map<UUID, Long> mapItems(Cart cart) {
        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            return new HashMap<>();
        }

        return cart.getItems().stream()
                .collect(Collectors.toMap(
                        item -> item.getId().getProductId(),
                        CartItem::getQuantity,
                        (a, b) -> a,
                        HashMap::new
                ));
    }
}
