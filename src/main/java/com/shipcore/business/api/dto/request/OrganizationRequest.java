package com.shipcore.business.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
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
        String phone,

        @NotBlank(message = "El pais es obligatorio.")
        @Size(min = 2, max = 2)
        String country,

        @NotBlank(message = "El plan es obligatorio.")
        @Size(max = 30)
        String plan,

        @NotNull(message = "El limite blando es obligatorio.")
        @PositiveOrZero(message = "El limite blando no puede ser negativo.")
        Integer softLimit,

        @NotNull(message = "El limite duro es obligatorio.")
        @PositiveOrZero(message = "El limite duro no puede ser negativo.")
        Integer hardLimit,

        @NotNull(message = "El uso actual es obligatorio.")
        @PositiveOrZero(message = "El uso actual no puede ser negativo.")
        Integer currentUsage
) {
}
