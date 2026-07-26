package com.shipcore.organization.service.impl;

import com.shipcore.organization.dto.request.OrganizationRequest;
import com.shipcore.organization.dto.response.OrganizationResponse;
import com.shipcore.organization.entity.Organization;
import com.shipcore.organization.mapper.OrganizationMapper;
import com.shipcore.organization.repository.OrganizationRepository;
import com.shipcore.organization.service.OrganizationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.shipcore.common.exception.ResourceAlreadyExistsException;
import com.shipcore.common.exception.ResourceNotFoundException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrganizationServiceImpl implements OrganizationService {

    private final OrganizationRepository organizationRepository;
    private final OrganizationMapper organizationMapper;

    @Override
    public OrganizationResponse create(OrganizationRequest request) {

        if (organizationRepository.existsByRuc(request.getRuc())) {
            throw new ResourceAlreadyExistsException("El RUC ya está registrado.");
        }

        Organization organization = organizationMapper.toEntity(request);

        organization.setActive(true);

        organization = organizationRepository.save(organization);

        return organizationMapper.toResponse(organization);

    }

    @Override
    public List<OrganizationResponse> findAll() {

        return organizationRepository.findAll()
                .stream()
                .map(organizationMapper::toResponse)
                .toList();

    }

    @Override
    public OrganizationResponse findById(Long id) {

        Organization organization = organizationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Organización no encontrada."));

        return organizationMapper.toResponse(organization);

    }

    @Override
    public OrganizationResponse update(Long id, OrganizationRequest request) {

        Organization organization = organizationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Organización no encontrada."));

        organizationMapper.updateEntity(request, organization);

        organization = organizationRepository.save(organization);

        return organizationMapper.toResponse(organization);

    }

    @Override
    public void delete(Long id) {

        Organization organization = organizationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Organización no encontrada."));

        organizationRepository.delete(organization);

    }

}