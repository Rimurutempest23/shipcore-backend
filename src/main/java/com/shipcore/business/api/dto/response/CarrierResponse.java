package com.shipcore.business.api.dto.response;

public record CarrierResponse(
        Long id,
        String name,
        String serviceType,
        String contactEmail,
        String phone,
        Boolean active,
        Long organizationId,
        String organizationName
) {
}
