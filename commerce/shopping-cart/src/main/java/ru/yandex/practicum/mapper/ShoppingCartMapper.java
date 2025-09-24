package ru.yandex.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.yandex.practicum.cart.ShoppingCartDto;
import ru.yandex.practicum.model.Cart;
import ru.yandex.practicum.model.CartItem;

import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface ShoppingCartMapper {

    ShoppingCartMapper INSTANCE = Mappers.getMapper(ShoppingCartMapper.class);

    @Mapping(target = "shoppingCartId", source = "cartId")
    @Mapping(target = "products", expression = "java(mapItems(cart))")
    ShoppingCartDto toDto(Cart cart);

    default Map<UUID, Long> mapItems(Cart cart) {
        if (cart.getItems() == null) {
            return Map.of();
        }
        return cart.getItems().stream()
                .collect(Collectors.toMap(
                        item -> item.getId().getProductId(),
                        CartItem::getQuantity
                ));
    }
}
