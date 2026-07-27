package com.shipcore.business.api.dto.request;

import com.shipcore.business.domain.enums.RateStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CarrierRateRequest(
        @NotBlank(message = "La zona es obligatoria.")
        @Size(max = 60)
        String zone,

        @NotNull(message = "El peso minimo es obligatorio.")
        @DecimalMin(value = "0.00", message = "El peso minimo no puede ser negativo.")
        BigDecimal minWeightKg,

        @NotNull(message = "El peso maximo es obligatorio.")
        @DecimalMin(value = "0.01", message = "El peso maximo debe ser mayor a cero.")
        BigDecimal maxWeightKg,

        @NotNull(message = "El precio base es obligatorio.")
        @DecimalMin(value = "0.00", message = "El precio base no puede ser negativo.")
        BigDecimal basePrice,

        @NotNull(message = "El precio por kilo es obligatorio.")
        @DecimalMin(value = "0.00", message = "El precio por kilo no puede ser negativo.")
        BigDecimal pricePerKg,

        @NotNull(message = "La fecha de inicio es obligatoria.")
        LocalDate validFrom,

        @NotNull(message = "La fecha de fin es obligatoria.")
        LocalDate validTo,

        @NotNull(message = "La version es obligatoria.")
        @Min(value = 1, message = "La version debe ser mayor a cero.")
        Integer versionNumber,

        @NotNull(message = "El estado es obligatorio.")
        RateStatus status,

        @NotNull(message = "El courier es obligatorio.")
        Long carrierId
) {
}
