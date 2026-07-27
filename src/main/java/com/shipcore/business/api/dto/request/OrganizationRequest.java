package com.shipcore.business.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record OrganizationRequest(
        @NotBlank(message = "El nombre es obligatorio.")
        @Size(max = 150)
        String name,

        @NotBlank(message = "El RUC es obligatorio.")
        @Size(min = 11, max = 11)
        String ruc,

        @Size(max = 255)
        String address,

        @Size(max = 20)
        String phone
) {
}
