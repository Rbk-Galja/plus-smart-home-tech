package ru.yandex.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.yandex.practicum.model.WarehouseAddress;
import ru.yandex.practicum.model.WarehouseProduct;
import ru.yandex.practicum.warehouse.AddressDto;
import ru.yandex.practicum.warehouse.BookedProductsDto;
import ru.yandex.practicum.warehouse.NewProductInWarehouseRequest;

@Mapper(componentModel = "spring")
public interface WarehouseMapper {
    WarehouseMapper INSTANCE = Mappers.getMapper(WarehouseMapper.class);

    @Mapping(target = "productId", source = "productId")
    @Mapping(target = "fragile", source = "fragile")
    @Mapping(target = "weight", source = "weight")
    @Mapping(target = "width", source = "dimension.width")
    @Mapping(target = "height", source = "dimension.height")
    @Mapping(target = "depth", source = "dimension.depth")
    @Mapping(target = "quantity", constant = "0L")
    WarehouseProduct toEntity(NewProductInWarehouseRequest dto);

    WarehouseAddress toEntity(AddressDto dto);

    AddressDto toDto(WarehouseAddress entity);

    BookedProductsDto toBookedProductsDto(double deliveryWeight, double deliveryVolume, boolean fragile);
}
