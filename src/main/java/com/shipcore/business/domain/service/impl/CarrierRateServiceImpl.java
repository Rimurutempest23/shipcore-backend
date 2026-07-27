package com.shipcore.business.domain.service.impl;

import com.shipcore.business.api.dto.request.CarrierRateRequest;
import com.shipcore.business.api.dto.response.CarrierRateResponse;
import com.shipcore.business.api.exception.BusinessRuleException;
import com.shipcore.business.api.exception.ResourceNotFoundException;
import com.shipcore.business.data.entity.Carrier;
import com.shipcore.business.data.entity.CarrierRate;
import com.shipcore.business.data.repository.CarrierRateRepository;
import com.shipcore.business.data.repository.CarrierRepository;
import com.shipcore.business.domain.mapper.CarrierRateMapper;
import com.shipcore.business.domain.service.CarrierRateService;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CarrierRateServiceImpl implements CarrierRateService {

    private final CarrierRateRepository carrierRateRepository;
    private final CarrierRepository carrierRepository;
    private final CarrierRateMapper carrierRateMapper;
    private final EntityManager entityManager;

    @Override
    public CarrierRateResponse create(CarrierRateRequest request) {
        validateRate(request);

        Carrier carrier = findCarrier(request.carrierId());
        CarrierRate rate = carrierRateMapper.toEntity(request);
        rate.setCarrier(carrier);
        rate.setOrganization(carrier.getOrganization());
        rate.setActive(true);

        CarrierRate saved = carrierRateRepository.save(rate);
        entityManager.flush();

        return carrierRateMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CarrierRateResponse> findAll() {
        return carrierRateRepository.findAllActiveWithCarrier()
                .stream()
                .map(carrierRateMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CarrierRateResponse findById(Long id) {
        return carrierRateMapper.toResponse(findRate(id));
    }

    @Override
    public CarrierRateResponse update(Long id, CarrierRateRequest request) {
        validateRate(request);

        CarrierRate rate = findRate(id);
        Carrier carrier = findCarrier(request.carrierId());

        carrierRateMapper.updateEntity(request, rate);
        rate.setCarrier(carrier);
        rate.setOrganization(carrier.getOrganization());

        return carrierRateMapper.toResponse(carrierRateRepository.save(rate));
    }

    @Override
    public void delete(Long id) {
        CarrierRate rate = findRate(id);
        rate.setActive(false);
        carrierRateRepository.save(rate);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CarrierRateResponse> findVersions(Long carrierId, String zone) {
        return carrierRateRepository.findVersionsByCarrierAndZone(carrierId, zone)
                .stream()
                .map(carrierRateMapper::toResponse)
                .toList();
    }

    private void validateRate(CarrierRateRequest request) {
        if (request.maxWeightKg().compareTo(request.minWeightKg()) < 0) {
            throw new BusinessRuleException("El peso maximo debe ser mayor o igual al peso minimo.");
        }

        if (request.validTo().isBefore(request.validFrom())) {
            throw new BusinessRuleException("La fecha final debe ser posterior o igual a la fecha inicial.");
        }

        if (request.transitDaysMax() < request.transitDaysMin()) {
            throw new BusinessRuleException("Los dias maximos de transito deben ser mayores o iguales a los dias minimos.");
        }
    }

    private Carrier findCarrier(Long id) {
        return carrierRepository.findByIdWithOrganization(id)
                .orElseThrow(() -> new ResourceNotFoundException("Courier no encontrado."));
    }

    private CarrierRate findRate(Long id) {
        return carrierRateRepository.findByIdWithCarrier(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tarifa no encontrada."));
    }

}
