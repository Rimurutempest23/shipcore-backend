package com.shipcore.business.domain.service.impl;

import com.shipcore.business.api.dto.request.OrganizationRequest;
import com.shipcore.business.api.dto.response.OrganizationResponse;
import com.shipcore.business.api.exception.ResourceAlreadyExistsException;
import com.shipcore.business.api.exception.ResourceNotFoundException;
import com.shipcore.business.data.entity.Organization;
import com.shipcore.business.data.repository.OrganizationRepository;
import com.shipcore.business.domain.mapper.OrganizationMapper;
import com.shipcore.business.domain.service.OrganizationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class OrganizationServiceImpl implements OrganizationService {

    private final OrganizationRepository organizationRepository;
    private final OrganizationMapper organizationMapper;

    @Override
    public OrganizationResponse create(OrganizationRequest request) {
        if (organizationRepository.existsByRuc(request.ruc())) {
            throw new ResourceAlreadyExistsException("El RUC ya esta registrado.");
        }

        Organization organization = organizationMapper.toEntity(request);
        organization.setActive(true);

        return organizationMapper.toResponse(organizationRepository.save(organization));
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrganizationResponse> findAll() {
        return organizationRepository.findAllActive()
                .stream()
                .map(organizationMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public OrganizationResponse findById(Long id) {
        return organizationMapper.toResponse(findOrganization(id));
    }

    @Override
    public OrganizationResponse update(Long id, OrganizationRequest request) {
        Organization organization = findOrganization(id);

        if (!organization.getRuc().equals(request.ruc())
                && organizationRepository.existsByRuc(request.ruc())) {
            throw new ResourceAlreadyExistsException("El RUC ya esta registrado.");
        }

        organizationMapper.updateEntity(request, organization);

        return organizationMapper.toResponse(organizationRepository.save(organization));
    }

    @Override
    public void delete(Long id) {
        Organization organization = findOrganization(id);
        organization.setActive(false);
        organizationRepository.save(organization);
    }

    private Organization findOrganization(Long id) {
        return organizationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Organizacion no encontrada."));
    }

}
