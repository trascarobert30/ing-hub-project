package com.ing.core.services.mapper;

import com.ing.core.services.data.Product;
import com.ing.core.services.dto.ProductRequest;
import com.ing.core.services.dto.ProductResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    Product toEntity(ProductRequest dto);
    ProductResponse toResponse(Product product);
}
