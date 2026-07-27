package com.shipcore.business.domain.mapper;

import com.shipcore.business.api.dto.request.CarrierRequest;
import com.shipcore.business.api.dto.response.CarrierResponse;
import com.shipcore.business.data.entity.Carrier;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CarrierMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "organization", ignore = true)
    @Mapping(target = "rates", ignore = true)
    Carrier toEntity(CarrierRequest request);

    @Mapping(target = "organizationId", source = "organization.id")
    @Mapping(target = "organizationName", source = "organization.name")
    CarrierResponse toResponse(Carrier carrier);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "organization", ignore = true)
    @Mapping(target = "rates", ignore = true)
    void updateEntity(CarrierRequest request, @MappingTarget Carrier carrier);

}
