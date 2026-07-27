package com.shipcore.business.domain.mapper;

import com.shipcore.business.api.dto.request.UserRequest;
import com.shipcore.business.api.dto.response.UserResponse;
import com.shipcore.business.data.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "password", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "organization", ignore = true)
    @Mapping(target = "profile", ignore = true)
    @Mapping(target = "quotes", ignore = true)
    User toEntity(UserRequest request);

    @Mapping(target = "organizationId", source = "organization.id")
    @Mapping(target = "organizationName", source = "organization.name")
    @Mapping(target = "phone", source = "profile.phone")
    @Mapping(target = "address", source = "profile.address")
    @Mapping(target = "bio", source = "profile.bio")
    UserResponse toResponse(User user);

    @Mapping(target = "password", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "organization", ignore = true)
    @Mapping(target = "profile", ignore = true)
    @Mapping(target = "quotes", ignore = true)
    void updateEntity(UserRequest request,
                      @MappingTarget User user);

}
