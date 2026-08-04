package com.shipcore.business.api.dto.response;

import com.shipcore.business.domain.enums.ApiEnv;

import java.time.LocalDateTime;

public record ApiKeyResponse(
        Long id,
        ApiEnv environment,
        String keyPreview,
        String secretKey, // Only returned on creation
        Integer quotaLimit,
        Integer usageCount,
        LocalDateTime lastUsedAt,
        LocalDateTime createdAt,
        Boolean active,
        Long organizationId
) {
}
