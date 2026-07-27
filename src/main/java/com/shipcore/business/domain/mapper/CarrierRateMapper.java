package com.shipcore.business.domain.mapper;

import com.shipcore.business.api.dto.request.CarrierRateRequest;
import com.shipcore.business.api.dto.response.CarrierRateResponse;
import com.shipcore.business.data.entity.CarrierRate;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CarrierRateMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "lockVersion", ignore = true)
    @Mapping(target = "carrier", ignore = true)
    @Mapping(target = "organization", ignore = true)
    CarrierRate toEntity(CarrierRateRequest request);

    @Mapping(target = "carrierId", source = "carrier.id")
    @Mapping(target = "carrierName", source = "carrier.name")
    @Mapping(target = "organizationId", source = "organization.id")
    CarrierRateResponse toResponse(CarrierRate rate);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "lockVersion", ignore = true)
    @Mapping(target = "carrier", ignore = true)
    @Mapping(target = "organization", ignore = true)
    void updateEntity(CarrierRateRequest request, @MappingTarget CarrierRate rate);

}
