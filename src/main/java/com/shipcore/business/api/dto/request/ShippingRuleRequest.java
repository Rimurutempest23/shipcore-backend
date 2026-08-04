package com.shipcore.business.api.dto.request;

import com.shipcore.business.domain.enums.RuleAction;
import com.shipcore.business.domain.enums.RuleField;
import com.shipcore.business.domain.enums.RuleOperator;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record ShippingRuleRequest(
        @NotBlank(message = "El nombre de la regla es obligatorio.")
        @Size(max = 120)
        String name,

        @NotNull(message = "El campo a evaluar es obligatorio.")
        RuleField field,

        @NotNull(message = "El operador es obligatorio.")
        RuleOperator operator,

        @NotBlank(message = "El valor a evaluar es obligatorio.")
        @Size(max = 120)
        String value,

        @NotNull(message = "La accion a ejecutar es obligatoria.")
        RuleAction action,

        @NotNull(message = "El valor de la accion es obligatorio.")
        @DecimalMin(value = "0.00", message = "El valor de la accion no puede ser negativo.")
        BigDecimal actionValue,

        @NotNull(message = "La prioridad es obligatoria.")
        @Min(value = 0, message = "La prioridad no puede ser negativa.")
        Integer priority
) {
}
