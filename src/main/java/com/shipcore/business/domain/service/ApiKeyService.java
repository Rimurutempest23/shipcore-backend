package com.shipcore.business.domain.service;

import com.shipcore.business.api.dto.request.ApiKeyRequest;
import com.shipcore.business.api.dto.response.ApiKeyResponse;
import com.shipcore.business.data.entity.ApiKey;

import java.util.List;

public interface ApiKeyService {

    ApiKeyResponse create(ApiKeyRequest request, String userEmail);

    List<ApiKeyResponse> findAll(String userEmail);

    void delete(Long id, String userEmail);

    ApiKey authenticate(String rawApiKey);

    void recordUsage(ApiKey apiKey);

}
