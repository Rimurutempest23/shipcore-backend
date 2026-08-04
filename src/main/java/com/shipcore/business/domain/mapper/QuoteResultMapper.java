package com.shipcore.business.domain.mapper;

import com.shipcore.business.api.dto.response.QuoteResultResponse;
import com.shipcore.business.data.entity.QuoteResult;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface QuoteResultMapper {

    @Mapping(target = "carrierId", source = "carrier.id")
    @Mapping(target = "carrierName", source = "carrier.name")
    @Mapping(target = "carrierRateId", source = "carrierRate.id")
    QuoteResultResponse toResponse(QuoteResult result);

}
