package com.shipcore.business.api.controller;

import com.shipcore.business.api.dto.request.CarrierRequest;
import com.shipcore.business.api.dto.response.CarrierResponse;
import com.shipcore.business.domain.service.CarrierService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/carriers")
@RequiredArgsConstructor
public class CarrierController {

    private final CarrierService carrierService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CarrierResponse> create(@Valid @RequestBody CarrierRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(carrierService.create(request));
    }

    @GetMapping
    public ResponseEntity<List<CarrierResponse>> findAll() {
        return ResponseEntity.ok(carrierService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CarrierResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(carrierService.findById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CarrierResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody CarrierRequest request) {
        return ResponseEntity.ok(carrierService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        carrierService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
