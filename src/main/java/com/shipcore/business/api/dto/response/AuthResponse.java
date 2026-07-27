package com.shipcore.business.api.dto.response;

public record AuthResponse(
        String accessToken,
        String tokenType,
        String message
) {
}
