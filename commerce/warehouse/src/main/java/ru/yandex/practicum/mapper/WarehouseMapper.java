package ru.yandex.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.yandex.practicum.model.WarehouseAddress;
import ru.yandex.practicum.model.WarehouseProduct;
import ru.yandex.practicum.warehouse.AddressDto;
import ru.yandex.practicum.warehouse.BookedProductsDto;
import ru.yandex.practicum.warehouse.NewProductInWarehouseRequest;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING)
public interface WarehouseMapper {
    @Mapping(target = "width", source = "dimension.width")
    @Mapping(target = "height", source = "dimension.height")
    @Mapping(target = "depth", source = "dimension.depth")
    @Mapping(target = "quantity", constant = "0L")
    WarehouseProduct toEntity(NewProductInWarehouseRequest dto);

    WarehouseAddress toEntity(AddressDto dto);

    AddressDto toDto(WarehouseAddress entity);

    BookedProductsDto toBookedProductsDto(Double deliveryWeight, Double deliveryVolume, Boolean fragile);
}
