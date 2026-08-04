package com.shipcore.business.api.controller;

import com.shipcore.business.api.dto.request.ApiKeyRequest;
import com.shipcore.business.api.dto.response.ApiKeyResponse;
import com.shipcore.business.domain.service.ApiKeyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/org/api-keys")
@RequiredArgsConstructor
public class ApiKeyController {

    private final ApiKeyService apiKeyService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiKeyResponse> create(
            @Valid @RequestBody ApiKeyRequest request,
            Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(apiKeyService.create(request, authentication.getName()));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ApiKeyResponse>> findAll(Authentication authentication) {
        return ResponseEntity.ok(apiKeyService.findAll(authentication.getName()));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            Authentication authentication) {
        apiKeyService.delete(id, authentication.getName());
        return ResponseEntity.noContent().build();
    }

}
