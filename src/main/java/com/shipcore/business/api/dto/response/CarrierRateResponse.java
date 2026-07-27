package com.shipcore.business.api.dto.response;

import com.shipcore.business.domain.enums.RateStatus;
import com.shipcore.business.domain.enums.RateSource;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CarrierRateResponse(
        Long id,
        String zone,
        String serviceType,
        BigDecimal minWeightKg,
        BigDecimal maxWeightKg,
        BigDecimal basePrice,
        BigDecimal pricePerKg,
        BigDecimal pricePerKm,
        Integer transitDaysMin,
        Integer transitDaysMax,
        LocalDate validFrom,
        LocalDate validTo,
        Integer versionNumber,
        RateStatus status,
        RateSource source,
        Boolean active,
        Long carrierId,
        String carrierName,
        Long organizationId
) {
}
