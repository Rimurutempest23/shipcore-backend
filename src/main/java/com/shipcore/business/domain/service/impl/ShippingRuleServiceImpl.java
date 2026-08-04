package com.shipcore.business.domain.service.impl;

import com.shipcore.business.api.dto.request.ShippingRuleRequest;
import com.shipcore.business.api.dto.response.ShippingRuleResponse;
import com.shipcore.business.api.exception.ResourceNotFoundException;
import com.shipcore.business.data.entity.Organization;
import com.shipcore.business.data.entity.ShippingRule;
import com.shipcore.business.data.entity.User;
import com.shipcore.business.data.repository.ShippingRuleRepository;
import com.shipcore.business.data.repository.UserRepository;
import com.shipcore.business.domain.mapper.ShippingRuleMapper;
import com.shipcore.business.domain.service.ShippingRuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ShippingRuleServiceImpl implements ShippingRuleService {

    private final ShippingRuleRepository shippingRuleRepository;
    private final UserRepository userRepository;
    private final ShippingRuleMapper shippingRuleMapper;

    @Override
    @CacheEvict(value = "rules", allEntries = true)
    public ShippingRuleResponse create(ShippingRuleRequest request, String userEmail) {
        User user = findUser(userEmail);
        Organization organization = user.getOrganization();

        ShippingRule rule = shippingRuleMapper.toEntity(request);
        rule.setOrganization(organization);
        rule.setActive(true);

        return shippingRuleMapper.toResponse(shippingRuleRepository.save(rule));
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "rules", key = "#userEmail")
    public List<ShippingRuleResponse> findAll(String userEmail) {
        User user = findUser(userEmail);
        return shippingRuleRepository.findAllActiveByOrganizationId(user.getOrganization().getId())
                .stream()
                .map(shippingRuleMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ShippingRuleResponse findById(Long id, String userEmail) {
        User user = findUser(userEmail);
        ShippingRule rule = shippingRuleRepository.findByIdAndOrganizationId(id, user.getOrganization().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Regla de envio no encontrada."));
        return shippingRuleMapper.toResponse(rule);
    }

    @Override
    @CacheEvict(value = "rules", allEntries = true)
    public ShippingRuleResponse update(Long id, ShippingRuleRequest request, String userEmail) {
        User user = findUser(userEmail);
        ShippingRule rule = shippingRuleRepository.findByIdAndOrganizationId(id, user.getOrganization().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Regla de envio no encontrada."));

        shippingRuleMapper.updateEntity(request, rule);
        return shippingRuleMapper.toResponse(shippingRuleRepository.save(rule));
    }

    @Override
    @CacheEvict(value = "rules", allEntries = true)
    public void delete(Long id, String userEmail) {
        User user = findUser(userEmail);
        ShippingRule rule = shippingRuleRepository.findByIdAndOrganizationId(id, user.getOrganization().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Regla de envio no encontrada."));
        rule.setActive(false);
        shippingRuleRepository.save(rule);
    }

    private User findUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado."));
    }

}
