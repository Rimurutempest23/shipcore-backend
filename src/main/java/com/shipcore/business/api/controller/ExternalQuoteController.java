package com.shipcore.business.api.controller;

import com.shipcore.business.api.dto.request.QuoteRequest;
import com.shipcore.business.api.dto.response.QuoteResponse;
import com.shipcore.business.data.entity.ApiKey;
import com.shipcore.business.domain.service.ApiKeyService;
import com.shipcore.business.domain.service.QuoteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/external/quotes")
@RequiredArgsConstructor
public class ExternalQuoteController {

    private final ApiKeyService apiKeyService;
    private final QuoteService quoteService;

    @PostMapping
    public ResponseEntity<QuoteResponse> create(
            @RequestHeader("X-API-Key") String rawApiKey,
            @Valid @RequestBody QuoteRequest request) {

        ApiKey apiKey = apiKeyService.authenticate(rawApiKey);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(quoteService.createQuoteFromApiKey(request, apiKey));
    }
}
