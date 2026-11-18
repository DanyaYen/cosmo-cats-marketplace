package com.cosmocats.marketplace.mapper;

import com.cosmocats.marketplace.domain.Product;
import com.cosmocats.marketplace.entity.ProductEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductEntityMapper {

    @Mapping(target = "categoryName", source = "category.name")
    Product toDomain(ProductEntity entity);

    @Mapping(target = "category", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    ProductEntity toEntity(Product domain);
}
