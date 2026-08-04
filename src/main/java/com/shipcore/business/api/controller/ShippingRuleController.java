package com.shipcore.business.api.controller;

import com.shipcore.business.api.dto.request.ShippingRuleRequest;
import com.shipcore.business.api.dto.response.ShippingRuleResponse;
import com.shipcore.business.domain.service.ShippingRuleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/rules")
@RequiredArgsConstructor
public class ShippingRuleController {

    private final ShippingRuleService shippingRuleService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ShippingRuleResponse> create(
            @Valid @RequestBody ShippingRuleRequest request,
            Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(shippingRuleService.create(request, authentication.getName()));
    }

    @GetMapping
    public ResponseEntity<List<ShippingRuleResponse>> findAll(Authentication authentication) {
        return ResponseEntity.ok(shippingRuleService.findAll(authentication.getName()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ShippingRuleResponse> findById(
            @PathVariable Long id,
            Authentication authentication) {
        return ResponseEntity.ok(shippingRuleService.findById(id, authentication.getName()));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ShippingRuleResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody ShippingRuleRequest request,
            Authentication authentication) {
        return ResponseEntity.ok(shippingRuleService.update(id, request, authentication.getName()));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            Authentication authentication) {
        shippingRuleService.delete(id, authentication.getName());
        return ResponseEntity.noContent().build();
    }

}
