package com.shipcore.business.api.dto.response;

import com.shipcore.business.domain.enums.QuoteStatus;
import com.shipcore.business.domain.enums.ServiceType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record QuoteResponse(
        Long id,
        String origin,
        String destination,
        String originZone,
        String destZone,
        BigDecimal packageWeightKg,
        BigDecimal distanceKm,
        BigDecimal lengthCm,
        BigDecimal widthCm,
        BigDecimal heightCm,
        ServiceType serviceType,
        String status,
        QuoteStatus quoteStatus,
        LocalDateTime createdAt,
        Long createdByUserId,
        String createdByUserName,
        Long organizationId,
        List<QuoteResultResponse> results
) {
}
