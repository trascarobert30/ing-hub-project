package com.elbit.core.services.mapper;

import com.elbit.core.services.data.User;
import com.elbit.core.services.dto.UserRequest;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toEntity(UserRequest dto);
}