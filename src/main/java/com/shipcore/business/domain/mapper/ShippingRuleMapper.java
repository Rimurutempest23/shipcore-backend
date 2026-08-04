package com.shipcore.business.domain.mapper;

import com.shipcore.business.api.dto.request.ShippingRuleRequest;
import com.shipcore.business.api.dto.response.ShippingRuleResponse;
import com.shipcore.business.data.entity.ShippingRule;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ShippingRuleMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "organization", ignore = true)
    @Mapping(target = "active", ignore = true)
    ShippingRule toEntity(ShippingRuleRequest request);

    @Mapping(target = "organizationId", source = "organization.id")
    ShippingRuleResponse toResponse(ShippingRule rule);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "organization", ignore = true)
    @Mapping(target = "active", ignore = true)
    void updateEntity(ShippingRuleRequest request, @MappingTarget ShippingRule rule);

}
