package com.ing.core.services.mapper;

import com.ing.core.services.data.User;
import com.ing.core.services.dto.UserRequest;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toEntity(UserRequest dto);
}