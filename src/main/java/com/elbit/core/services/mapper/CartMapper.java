package com.elbit.core.services.mapper;

import com.elbit.core.services.data.Cart;
import com.elbit.core.services.dto.CartResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CartMapper {
    CartResponse toResponse(Cart cart);
}
