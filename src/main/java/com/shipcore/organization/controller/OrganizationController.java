package com.shipcore.organization.controller;

import com.shipcore.organization.dto.request.OrganizationRequest;
import com.shipcore.organization.dto.response.OrganizationResponse;
import com.shipcore.organization.service.OrganizationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(
            @PathVariable Long id) {

        organizationService.delete(id);

        return ResponseEntity.noContent().build();

    }

}