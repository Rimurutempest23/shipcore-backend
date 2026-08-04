package com.shipcore.business.api.controller;

import com.shipcore.business.api.dto.request.QuoteRequest;
import com.shipcore.business.api.dto.response.PageResponse;
import com.shipcore.business.api.dto.response.QuoteResponse;
import com.shipcore.business.domain.service.QuoteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/quotes")
@RequiredArgsConstructor
public class QuoteController {

    private final QuoteService quoteService;

    @PostMapping
    public ResponseEntity<QuoteResponse> create(
            @Valid @RequestBody QuoteRequest request,
            Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(quoteService.createQuote(request, authentication.getName()));
    }

    @GetMapping
    public ResponseEntity<PageResponse<QuoteResponse>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateTo,
            @RequestParam(required = false) Long carrierId,
            @RequestParam(required = false) String search,
            Authentication authentication) {

        PageRequest pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(quoteService.findQuotes(
                authentication.getName(), status, dateFrom, dateTo, carrierId, search, pageable
        ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<QuoteResponse> findById(
            @PathVariable Long id,
            Authentication authentication) {
        return ResponseEntity.ok(quoteService.findById(id, authentication.getName()));
    }

    @PostMapping("/{id}/results/{resultId}/select")
    public ResponseEntity<QuoteResponse> selectResult(
            @PathVariable Long id,
            @PathVariable Long resultId,
            Authentication authentication) {
        return ResponseEntity.ok(quoteService.selectQuoteResult(id, resultId, authentication.getName()));
    }

}
