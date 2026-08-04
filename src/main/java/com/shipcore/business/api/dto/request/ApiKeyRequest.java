package com.shipcore.business.api.dto.request;

import com.shipcore.business.domain.enums.ApiEnv;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record ApiKeyRequest(
        @NotNull(message = "El ambiente es obligatorio.")
        ApiEnv environment,

        @NotNull(message = "El limite de cuota es obligatorio.")
        @Min(value = 1, message = "El limite de cuota debe ser al menos 1.")
        Integer quotaLimit
) {
}
