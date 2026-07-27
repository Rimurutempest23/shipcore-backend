package com.shipcore.business.api.dto.response;

public record CarrierResponse(
        Long id,
        String name,
        String code,
        String serviceType,
        String logoUrl,
        String contactEmail,
        String phone,
        Boolean active,
        Long organizationId,
        String organizationName
) {
}
