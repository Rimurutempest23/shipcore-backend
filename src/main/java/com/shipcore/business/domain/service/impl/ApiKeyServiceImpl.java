package com.shipcore.business.domain.service.impl;

import com.shipcore.business.api.dto.request.ApiKeyRequest;
import com.shipcore.business.api.dto.response.ApiKeyResponse;
import com.shipcore.business.api.exception.BusinessRuleException;
import com.shipcore.business.api.exception.ResourceNotFoundException;
import com.shipcore.business.data.entity.ApiKey;
import com.shipcore.business.data.entity.Organization;
import com.shipcore.business.data.entity.User;
import com.shipcore.business.data.repository.ApiKeyRepository;
import com.shipcore.business.data.repository.UserRepository;
import com.shipcore.business.domain.enums.ApiEnv;
import com.shipcore.business.domain.mapper.ApiKeyMapper;
import com.shipcore.business.domain.service.ApiKeyService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ApiKeyServiceImpl implements ApiKeyService {

    private final ApiKeyRepository apiKeyRepository;
    private final UserRepository userRepository;
    private final ApiKeyMapper apiKeyMapper;
    private final PasswordEncoder passwordEncoder;
    private final com.shipcore.security.hmac.HmacService hmacService;

    @Override
    public ApiKeyResponse create(ApiKeyRequest request, String userEmail) {
        User user = findUser(userEmail);
        Organization organization = user.getOrganization();

        String prefix = request.environment() == ApiEnv.PROD ? "sc_live_" : "sc_test_";
        String rawSecret = prefix + UUID.randomUUID().toString().replace("-", "");
        String preview = rawSecret.substring(0, 12) + "...";
        
        // Seguridad dual: Hash BCrypt (bhash) + Firma HMAC-SHA256 con Frase Segura
        String hmacSignature = hmacService.calculateHmac(rawSecret);
        String hash = passwordEncoder.encode(hmacSignature);

        ApiKey apiKey = apiKeyMapper.toEntity(request);
        apiKey.setKeyPreview(preview);
        apiKey.setKeyHash(hash);
        apiKey.setUsageCount(0);
        apiKey.setOrganization(organization);
        apiKey.setActive(true);

        ApiKey saved = apiKeyRepository.save(apiKey);

        return new ApiKeyResponse(
                saved.getId(),
                saved.getEnvironment(),
                saved.getKeyPreview(),
                rawSecret, // Return plain secret key ONCE upon creation
                saved.getQuotaLimit(),
                saved.getUsageCount(),
                saved.getLastUsedAt(),
                saved.getCreatedAt(),
                saved.getActive(),
                organization.getId()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<ApiKeyResponse> findAll(String userEmail) {
        User user = findUser(userEmail);
        return apiKeyRepository.findAllActiveByOrganizationId(user.getOrganization().getId())
                .stream()
                .map(apiKeyMapper::toResponse)
                .toList();
    }

    @Override
    public void delete(Long id, String userEmail) {
        User user = findUser(userEmail);
        ApiKey apiKey = apiKeyRepository.findByIdAndOrganizationId(id, user.getOrganization().getId())
                .orElseThrow(() -> new ResourceNotFoundException("API Key no encontrada."));

        apiKey.setActive(false);
        apiKeyRepository.save(apiKey);
    }

    @Override
    @Transactional(readOnly = true)
    public ApiKey authenticate(String rawApiKey) {
        if (rawApiKey == null || rawApiKey.length() < 12) {
            throw new BusinessRuleException("API Key invalida.");
        }

        String preview = rawApiKey.substring(0, 12) + "...";
        ApiKey apiKey = apiKeyRepository.findByKeyPreviewAndActiveTrue(preview)
                .orElseThrow(() -> new BusinessRuleException("API Key invalida o revocada."));

        String hmacSignature = hmacService.calculateHmac(rawApiKey);
        if (!passwordEncoder.matches(hmacSignature, apiKey.getKeyHash())) {
            throw new BusinessRuleException("API Key invalida.");
        }

        if (apiKey.getUsageCount() >= apiKey.getQuotaLimit()) {
            throw new BusinessRuleException("La API Key alcanzo su cuota mensual.");
        }

        return apiKey;
    }

    @Override
    public void recordUsage(ApiKey apiKey) {
        apiKey.setUsageCount(apiKey.getUsageCount() + 1);
        apiKey.setLastUsedAt(LocalDateTime.now());
        apiKeyRepository.save(apiKey);
    }

    private User findUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado."));
    }

}
