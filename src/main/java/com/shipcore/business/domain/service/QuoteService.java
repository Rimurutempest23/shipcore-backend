package com.shipcore.business.domain.service;

import com.shipcore.business.api.dto.request.QuoteRequest;
import com.shipcore.business.api.dto.response.PageResponse;
import com.shipcore.business.api.dto.response.QuoteResponse;
import com.shipcore.business.data.entity.ApiKey;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

public interface QuoteService {

    QuoteResponse createQuote(QuoteRequest request, String userEmail);

    QuoteResponse createQuoteFromApiKey(QuoteRequest request, ApiKey apiKey);

    PageResponse<QuoteResponse> findQuotes(
            String userEmail,
            String status,
            LocalDateTime dateFrom,
            LocalDateTime dateTo,
            Long carrierId,
            String search,
            Pageable pageable);

    QuoteResponse findById(Long id, String userEmail);

    QuoteResponse selectQuoteResult(Long quoteId, Long resultId, String userEmail);

}
