package ru.yandex.practicum.mapper;

import org.mapstruct.Mapper;
import ru.yandex.practicum.model.Order;
import ru.yandex.practicum.order.OrderDto;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING)
public interface OrderMapper {
    Order toOrder(OrderDto orderDto);
    OrderDto toOrderDto(Order order);
}
