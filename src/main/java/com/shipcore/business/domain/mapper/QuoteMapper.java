package com.shipcore.business.domain.mapper;

import com.shipcore.business.api.dto.request.QuoteRequest;
import com.shipcore.business.api.dto.response.QuoteResponse;
import com.shipcore.business.data.entity.Quote;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {QuoteResultMapper.class})
public interface QuoteMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "quoteStatus", ignore = true)
    @Mapping(target = "organization", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "originAddress", ignore = true)
    @Mapping(target = "destinationAddress", ignore = true)
    @Mapping(target = "results", ignore = true)
    Quote toEntity(QuoteRequest request);

    @Mapping(target = "createdByUserId", source = "createdBy.id")
    @Mapping(target = "createdByUserName", expression = "java(quote.getCreatedBy() != null ? quote.getCreatedBy().getFirstName() + ' ' + quote.getCreatedBy().getLastName() : null)")
    @Mapping(target = "organizationId", source = "organization.id")
    QuoteResponse toResponse(Quote quote);

}
