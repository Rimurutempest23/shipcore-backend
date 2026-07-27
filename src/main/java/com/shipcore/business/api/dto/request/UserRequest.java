package com.shipcore.business.api.dto.request;

import com.shipcore.business.domain.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UserRequest(
        @NotBlank(message = "El nombre es obligatorio.")
        String firstName,

        @NotBlank(message = "El apellido es obligatorio.")
        String lastName,

        @Email(message = "Correo invalido.")
        @NotBlank(message = "El correo es obligatorio.")
        String email,

        @NotBlank(message = "La contrasena es obligatoria.")
        String password,

        @NotNull(message = "El rol es obligatorio.")
        Role role,

        @NotNull(message = "La organizacion es obligatoria.")
        Long organizationId
) {
}
