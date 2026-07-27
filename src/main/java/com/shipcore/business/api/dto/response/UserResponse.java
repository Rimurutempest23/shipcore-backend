package com.shipcore.business.api.dto.response;

import com.shipcore.business.domain.enums.Role;

public record UserResponse(
        Long id,
        String firstName,
        String lastName,
        String email,
        Role role,
        Boolean active,
        Long organizationId,
        String organizationName
) {
}
