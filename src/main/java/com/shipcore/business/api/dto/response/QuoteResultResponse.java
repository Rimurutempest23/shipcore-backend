package com.shipcore.business.api.dto.response;

import java.math.BigDecimal;

public record QuoteResultResponse(
        Long id,
        Long carrierId,
        String carrierName,
        Long carrierRateId,
        Integer rateVersionUsed,
        BigDecimal price,
        Integer transitDaysMin,
        Integer transitDaysMax,
        String restrictions,
        Boolean selected,
        Boolean preferred
) {
}
