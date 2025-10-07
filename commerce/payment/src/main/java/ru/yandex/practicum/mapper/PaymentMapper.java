package ru.yandex.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.yandex.practicum.model.Payment;
import ru.yandex.practicum.payment.PaymentDto;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING)
public interface PaymentMapper {
    @Mapping(source = "id", target = "paymentId")
    @Mapping(source = "totalPrice", target = "totalPayment")
    @Mapping(source = "deliveryPrice", target = "deliveryTotal")
    PaymentDto toDto(Payment payment);

    @Mapping(source = "paymentId", target = "id")
    @Mapping(source = "totalPayment", target = "totalPrice")
    @Mapping(source = "deliveryTotal", target = "deliveryPrice")
    @Mapping(target = "productPrice", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Payment toEntity(PaymentDto dto);
}
