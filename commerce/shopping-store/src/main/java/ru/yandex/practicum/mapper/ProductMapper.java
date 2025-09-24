package ru.yandex.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;
import ru.yandex.practicum.model.Product;
import ru.yandex.practicum.product.ProductDto;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    ProductMapper INSTANCE = Mappers.getMapper(ProductMapper.class);

    @Mapping(target = "productId", ignore = true)
    Product toProduct(ProductDto productDto);

    ProductDto toProductDto(Product product);


    @Mapping(target = "productId", ignore = true)
    void updateEntityFromDto(ProductDto productDto, @MappingTarget Product product);
}
