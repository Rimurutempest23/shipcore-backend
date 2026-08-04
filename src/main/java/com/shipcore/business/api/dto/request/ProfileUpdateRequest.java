package com.shipcore.business.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ProfileUpdateRequest(
        @NotBlank(message = "El nombre es obligatorio.")
        String firstName,

        @NotBlank(message = "El apellido es obligatorio.")
        String lastName,

        @Size(max = 20, message = "El teléfono no puede exceder 20 caracteres.")
        String phone,

        @Size(max = 200, message = "La dirección no puede exceder 200 caracteres.")
        String address,

        @Size(max = 500, message = "La biografía no puede exceder 500 caracteres.")
        String bio
) {
}
