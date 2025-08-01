package com.ing.core.services.mapper;

import com.ing.core.services.data.Cart;
import com.ing.core.services.dto.CartResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CartMapper {
    CartResponse toResponse(Cart cart);
}
