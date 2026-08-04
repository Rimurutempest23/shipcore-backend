package com.shipcore.business.api.dto.response;

import java.math.BigDecimal;

public record DashboardSummaryResponse(
        long totalQuotes,
        long monthlyQuotes,
        BigDecimal estimatedSavings,
        int currentUsage,
        int softLimit,
        int hardLimit
) {
}
