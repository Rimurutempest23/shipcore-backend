package com.shipcore.business.domain.service;

import com.shipcore.business.api.dto.response.DashboardSummaryResponse;

public interface DashboardService {

    DashboardSummaryResponse getSummary(String userEmail);

}
