package com.shipcore.business.domain.service.impl;

import com.shipcore.business.api.dto.request.OrganizationRequest;
import com.shipcore.business.api.dto.response.OrganizationResponse;
import com.shipcore.business.api.exception.ResourceAlreadyExistsException;
import com.shipcore.business.api.exception.ResourceNotFoundException;
import com.shipcore.business.data.entity.Organization;
import com.shipcore.business.data.entity.User;
import com.shipcore.business.data.repository.OrganizationRepository;
import com.shipcore.business.data.repository.UserRepository;
import com.shipcore.business.domain.mapper.OrganizationMapper;
import com.shipcore.business.domain.service.OrganizationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class OrganizationServiceImpl implements OrganizationService {

    private final OrganizationRepository organizationRepository;
    private final UserRepository userRepository;
    private final OrganizationMapper organizationMapper;

    @Override
    public OrganizationResponse create(OrganizationRequest request) {
        if (organizationRepository.existsByRuc(request.ruc())) {
            throw new ResourceAlreadyExistsException("El RUC ya esta registrado.");
        }

        Organization organization = organizationMapper.toEntity(request);
        applyPlanPolicy(organization);
        organization.setActive(true);

        return organizationMapper.toResponse(organizationRepository.save(organization));
    }

    @Override
    public List<OrganizationResponse> findAll() {
        return organizationRepository.findAllActive()
                .stream()
                .map(this::resetUsageIfDue)
                .map(organizationMapper::toResponse)
                .toList();
    }

    @Override
    public OrganizationResponse findCurrentUserOrganization(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado."));

        return organizationMapper.toResponse(resetUsageIfDue(user.getOrganization()));
    }

    @Override
    public OrganizationResponse findById(Long id) {
        return organizationMapper.toResponse(resetUsageIfDue(findOrganization(id)));
    }

    @Override
    public OrganizationResponse update(Long id, OrganizationRequest request) {
        Organization organization = findOrganization(id);

        if (!organization.getRuc().equals(request.ruc())
                && organizationRepository.existsByRuc(request.ruc())) {
            throw new ResourceAlreadyExistsException("El RUC ya esta registrado.");
        }

        organizationMapper.updateEntity(request, organization);
        applyPlanPolicy(organization);

        return organizationMapper.toResponse(organizationRepository.save(organization));
    }

    @Override
    public OrganizationResponse resetUsage(Long id) {
        Organization organization = findOrganization(id);
        organization.setCurrentUsage(0);
        applyPlanPolicy(organization);
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

    private void applyPlanPolicy(Organization organization) {
        String plan = organization.getPlan() == null ? "starter" : organization.getPlan().toLowerCase();
        organization.setPlan(plan);

        switch (plan) {
            case "enterprise" -> {
                organization.setSoftLimit(10000);
                organization.setHardLimit(12000);
            }
            case "growth" -> {
                organization.setSoftLimit(3000);
                organization.setHardLimit(3600);
            }
            default -> {
                organization.setPlan("starter");
                organization.setSoftLimit(1000);
                organization.setHardLimit(1200);
            }
        }
    }

    private Organization resetUsageIfDue(Organization organization) {
        LocalDateTime lastReset = organization.getUpdatedAt();
        if (lastReset == null) {
            return organizationRepository.save(organization);
        }

        String plan = organization.getPlan() == null ? "starter" : organization.getPlan().toLowerCase();
        LocalDateTime nextReset = switch (plan) {
            case "enterprise" -> lastReset.plusDays(1);
            case "growth" -> lastReset.plusWeeks(1);
            default -> lastReset.plusMonths(1);
        };

        if (LocalDateTime.now().isBefore(nextReset)) {
            return organization;
        }

        organization.setCurrentUsage(0);
        applyPlanPolicy(organization);
        return organizationRepository.save(organization);
    }

}
