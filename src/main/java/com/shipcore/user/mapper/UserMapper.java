package com.shipcore.user.mapper;

import com.shipcore.user.dto.request.UserRequest;
import com.shipcore.user.dto.response.UserResponse;
import com.shipcore.user.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "password", ignore = true)
    @Mapping(target = "enabled", ignore = true)
    User toEntity(UserRequest request);

    UserResponse toResponse(User user);

    @Mapping(target = "password", ignore = true)
    @Mapping(target = "enabled", ignore = true)
    void updateEntity(UserRequest request,
                      @MappingTarget User user);

}