package com.shipcore.business.api.dto.request;

import com.shipcore.business.domain.enums.ServiceType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record QuoteRequest(
        @NotBlank(message = "El origen es obligatorio.")
        @Size(max = 255)
        String origin,

        @NotBlank(message = "El destino es obligatorio.")
        @Size(max = 255)
        String destination,

        @NotBlank(message = "La zona de origen es obligatoria.")
        @Size(max = 60)
        String originZone,

        @NotBlank(message = "La zona de destino es obligatoria.")
        @Size(max = 60)
        String destZone,

        @NotNull(message = "El peso del paquete es obligatorio.")
        @DecimalMin(value = "0.01", message = "El peso del paquete debe ser mayor a cero.")
        BigDecimal packageWeightKg,

        @DecimalMin(value = "0.00", message = "La distancia no puede ser negativa.")
        BigDecimal distanceKm,

        @DecimalMin(value = "0.00", message = "El largo no puede ser negativo.")
        BigDecimal lengthCm,

        @DecimalMin(value = "0.00", message = "El ancho no puede ser negativo.")
        BigDecimal widthCm,

        @DecimalMin(value = "0.00", message = "El alto no puede ser negativo.")
        BigDecimal heightCm,

        ServiceType serviceType
) {
}
