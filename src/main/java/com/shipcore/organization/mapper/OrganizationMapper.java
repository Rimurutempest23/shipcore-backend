package com.shipcore.organization.mapper;

import com.shipcore.organization.dto.request.OrganizationRequest;
import com.shipcore.organization.dto.response.OrganizationResponse;
import com.shipcore.organization.entity.Organization;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface OrganizationMapper {

    Organization toEntity(OrganizationRequest request);

    OrganizationResponse toResponse(Organization organization);

    void updateEntity(OrganizationRequest request,
                      @MappingTarget Organization organization);

}