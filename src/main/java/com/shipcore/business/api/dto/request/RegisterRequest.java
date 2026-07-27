package com.shipcore.business.api.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank(message = "El nombre es obligatorio.")
        @Size(max = 100)
        String firstName,

        @NotBlank(message = "El apellido es obligatorio.")
        @Size(max = 100)
        String lastName,

        @Email(message = "Correo invalido.")
        @NotBlank(message = "El correo es obligatorio.")
        String email,

        @NotBlank(message = "La contrasena es obligatoria.")
        @Size(min = 8, max = 100)
        String password,

        @NotNull(message = "La organizacion es obligatoria.")
        Long organizationId
) {
}
