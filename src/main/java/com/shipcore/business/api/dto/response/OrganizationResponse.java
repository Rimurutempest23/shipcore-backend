package com.shipcore.business.api.dto.response;

public record OrganizationResponse(
        Long id,
        String name,
        String ruc,
        String address,
        String phone,
        Boolean active
) {
}
