package com.shipcore.business.domain.mapper;

import com.shipcore.business.api.dto.request.OrganizationRequest;
import com.shipcore.business.api.dto.response.OrganizationResponse;
import com.shipcore.business.data.entity.Organization;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface OrganizationMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "users", ignore = true)
    @Mapping(target = "carriers", ignore = true)
    @Mapping(target = "quotes", ignore = true)
    Organization toEntity(OrganizationRequest request);

    OrganizationResponse toResponse(Organization organization);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "users", ignore = true)
    @Mapping(target = "carriers", ignore = true)
    @Mapping(target = "quotes", ignore = true)
    void updateEntity(OrganizationRequest request,
                      @MappingTarget Organization organization);

}
