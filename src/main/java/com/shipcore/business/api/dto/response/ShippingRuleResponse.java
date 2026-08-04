package com.shipcore.business.api.dto.response;

import com.shipcore.business.domain.enums.RuleAction;
import com.shipcore.business.domain.enums.RuleField;
import com.shipcore.business.domain.enums.RuleOperator;

import java.math.BigDecimal;

public record ShippingRuleResponse(
        Long id,
        String name,
        RuleField field,
        RuleOperator operator,
        String value,
        RuleAction action,
        BigDecimal actionValue,
        Integer priority,
        Boolean active,
        Long organizationId
) {
}
