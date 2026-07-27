package com.shipcore.business.api.dto.response;

import com.shipcore.business.domain.enums.RateStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CarrierRateResponse(
        Long id,
        String zone,
        BigDecimal minWeightKg,
        BigDecimal maxWeightKg,
        BigDecimal basePrice,
        BigDecimal pricePerKg,
        LocalDate validFrom,
        LocalDate validTo,
        Integer versionNumber,
        RateStatus status,
        Boolean active,
        Long carrierId,
        String carrierName,
        Long organizationId
) {
}
