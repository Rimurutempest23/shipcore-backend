package com.shipcore.business.api.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CarrierRequest(
        @NotBlank(message = "El nombre del courier es obligatorio.")
        @Size(max = 120)
        String name,

        @NotBlank(message = "El tipo de servicio es obligatorio.")
        @Size(max = 60)
        String serviceType,

        @Email(message = "Correo invalido.")
        @Size(max = 80)
        String contactEmail,

        @Size(max = 20)
        String phone,

        @NotNull(message = "La organizacion es obligatoria.")
        Long organizationId
) {
}
