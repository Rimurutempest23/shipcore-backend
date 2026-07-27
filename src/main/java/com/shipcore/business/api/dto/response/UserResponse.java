package com.shipcore.business.api.dto.response;

import com.shipcore.business.domain.enums.Role;

public record UserResponse(
        Long id,
        String firstName,
        String lastName,
        String email,
        Role role,
        String phone,
        String address,
        String bio,
        Boolean active,
        Long organizationId,
        String organizationName
) {
}
