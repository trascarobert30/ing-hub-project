package com.elbit.core.services.mapper;

import com.elbit.core.services.data.Product;
import com.elbit.core.services.dto.ProductRequest;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    Product toEntity(ProductRequest dto);
}
