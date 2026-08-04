package com.shipcore.business.domain.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Role {

    ROLE_ADMIN("admin"),
    ROLE_OPERATOR("operador");

    private final String value;

    Role(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static Role fromValue(String value) {
        if (value == null) return ROLE_OPERATOR;
        for (Role r : Role.values()) {
            if (r.value.equalsIgnoreCase(value) || r.name().equalsIgnoreCase(value) || ("ROLE_" + r.value).equalsIgnoreCase(value)) {
                return r;
            }
        }
        return ROLE_OPERATOR;
    }

}
