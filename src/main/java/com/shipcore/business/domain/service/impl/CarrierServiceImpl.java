package com.shipcore.business.domain.service.impl;

import com.shipcore.business.api.dto.request.CarrierRequest;
import com.shipcore.business.api.dto.response.CarrierResponse;
import com.shipcore.business.api.exception.ResourceAlreadyExistsException;
import com.shipcore.business.api.exception.ResourceNotFoundException;
import com.shipcore.business.data.entity.Carrier;
import com.shipcore.business.data.entity.Organization;
import com.shipcore.business.data.repository.CarrierRepository;
import com.shipcore.business.data.repository.OrganizationRepository;
import com.shipcore.business.domain.mapper.CarrierMapper;
import com.shipcore.business.domain.service.CarrierService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CarrierServiceImpl implements CarrierService {

    private final CarrierRepository carrierRepository;
    private final OrganizationRepository organizationRepository;
    private final CarrierMapper carrierMapper;

    @Override
    public CarrierResponse create(CarrierRequest request) {
        if (carrierRepository.existsByOrganizationIdAndNameIgnoreCase(request.organizationId(), request.name())) {
            throw new ResourceAlreadyExistsException("El courier ya existe en la organizacion.");
        }

        Carrier carrier = carrierMapper.toEntity(request);
        carrier.setOrganization(findOrganization(request.organizationId()));
        carrier.setActive(true);

        return carrierMapper.toResponse(carrierRepository.save(carrier));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CarrierResponse> findAll() {
        return carrierRepository.findAllActiveWithOrganization()
                .stream()
                .map(carrierMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CarrierResponse findById(Long id) {
        return carrierMapper.toResponse(findCarrier(id));
    }

    @Override
    public CarrierResponse update(Long id, CarrierRequest request) {
        Carrier carrier = findCarrier(id);

        if (!carrier.getName().equalsIgnoreCase(request.name())
                && carrierRepository.existsByOrganizationIdAndNameIgnoreCase(request.organizationId(), request.name())) {
            throw new ResourceAlreadyExistsException("El courier ya existe en la organizacion.");
        }

        carrierMapper.updateEntity(request, carrier);
        carrier.setOrganization(findOrganization(request.organizationId()));

        return carrierMapper.toResponse(carrierRepository.save(carrier));
    }

    @Override
    public void delete(Long id) {
        Carrier carrier = findCarrier(id);
        carrier.setActive(false);
        carrierRepository.save(carrier);
    }

    private Carrier findCarrier(Long id) {
        return carrierRepository.findByIdWithOrganization(id)
                .orElseThrow(() -> new ResourceNotFoundException("Courier no encontrado."));
    }

    private Organization findOrganization(Long id) {
        return organizationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Organizacion no encontrada."));
    }

}
