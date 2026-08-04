package com.shipcore.business.domain.mapper;

import com.shipcore.business.api.dto.request.ApiKeyRequest;
import com.shipcore.business.api.dto.response.ApiKeyResponse;
import com.shipcore.business.data.entity.ApiKey;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ApiKeyMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "keyPreview", ignore = true)
    @Mapping(target = "keyHash", ignore = true)
    @Mapping(target = "usageCount", ignore = true)
    @Mapping(target = "lastUsedAt", ignore = true)
    @Mapping(target = "organization", ignore = true)
    ApiKey toEntity(ApiKeyRequest request);

    @Mapping(target = "organizationId", source = "organization.id")
    @Mapping(target = "secretKey", ignore = true)
    ApiKeyResponse toResponse(ApiKey apiKey);

}
