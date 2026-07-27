package com.shipcore.business.api.controller;

import com.shipcore.business.api.dto.request.CarrierRateRequest;
import com.shipcore.business.api.dto.response.CarrierRateResponse;
import com.shipcore.business.domain.service.CarrierRateService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
@RequestMapping("/api/v1/rates")
@RequiredArgsConstructor
public class CarrierRateController {

    private final CarrierRateService carrierRateService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CarrierRateResponse> create(@Valid @RequestBody CarrierRateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(carrierRateService.create(request));
    }

    @GetMapping
    public ResponseEntity<List<CarrierRateResponse>> findAll() {
        return ResponseEntity.ok(carrierRateService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CarrierRateResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(carrierRateService.findById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CarrierRateResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody CarrierRateRequest request) {
        return ResponseEntity.ok(carrierRateService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        carrierRateService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/carrier/{carrierId}/versions")
    public ResponseEntity<List<CarrierRateResponse>> findVersions(
            @PathVariable Long carrierId,
            @RequestParam @NotBlank(message = "La zona es obligatoria.") String zone) {
        return ResponseEntity.ok(carrierRateService.findVersions(carrierId, zone));
    }

}
