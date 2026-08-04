package com.shipcore.business.domain.service;

import com.shipcore.business.api.dto.request.ShippingRuleRequest;
import com.shipcore.business.api.dto.response.ShippingRuleResponse;

import java.util.List;

public interface ShippingRuleService {

    ShippingRuleResponse create(ShippingRuleRequest request, String userEmail);

    List<ShippingRuleResponse> findAll(String userEmail);

    ShippingRuleResponse findById(Long id, String userEmail);

    ShippingRuleResponse update(Long id, ShippingRuleRequest request, String userEmail);

    void delete(Long id, String userEmail);

}
