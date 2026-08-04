package com.shipcore.business.api.dto.response;

import java.time.LocalDateTime;

public record OrganizationResponse(
        Long id,
        String name,
        String ruc,
        String address,
        String phone,
        String country,
        String plan,
        Integer softLimit,
        Integer hardLimit,
        Integer currentUsage,
        LocalDateTime createdAt,
        Boolean active
) {
}
