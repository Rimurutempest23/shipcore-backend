package com.shipcore.business.api.controller;

import com.shipcore.business.api.dto.request.OrganizationRequest;
import com.shipcore.business.api.dto.response.OrganizationResponse;
import com.shipcore.business.domain.service.OrganizationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

@RestController
@RequestMapping("/api/v1/organizations")
@RequiredArgsConstructor
public class OrganizationController {

    private final OrganizationService organizationService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OrganizationResponse> create(
            @Valid @RequestBody OrganizationRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(organizationService.create(request));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<OrganizationResponse>> findAll() {

        return ResponseEntity.ok(
                organizationService.findAll()
        );

    }

    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR')")
    public ResponseEntity<OrganizationResponse> findCurrentUserOrganization(
            Authentication authentication) {

        return ResponseEntity.ok(
                organizationService.findCurrentUserOrganization(authentication.getName())
        );

    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OrganizationResponse> findById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                organizationService.findById(id)
        );

    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OrganizationResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody OrganizationRequest request) {

        return ResponseEntity.ok(
                organizationService.update(id, request)
        );

    }

    @PostMapping("/{id}/usage/reset")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OrganizationResponse> resetUsage(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                organizationService.resetUsage(id)
        );

    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(
            @PathVariable Long id) {

        organizationService.delete(id);

        return ResponseEntity.noContent().build();

    }

}
