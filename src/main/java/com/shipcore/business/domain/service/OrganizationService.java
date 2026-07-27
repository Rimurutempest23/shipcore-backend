package com.shipcore.business.domain.service;

import com.shipcore.business.api.dto.request.OrganizationRequest;
import com.shipcore.business.api.dto.response.OrganizationResponse;

import java.util.List;

public interface OrganizationService {

    OrganizationResponse create(OrganizationRequest request);

    List<OrganizationResponse> findAll();

    OrganizationResponse findById(Long id);

    OrganizationResponse update(Long id, OrganizationRequest request);

    void delete(Long id);

}
