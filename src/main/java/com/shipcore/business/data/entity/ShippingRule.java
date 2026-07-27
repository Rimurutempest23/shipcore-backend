package com.shipcore.business.data.entity;

import com.shipcore.business.domain.enums.RuleAction;
import com.shipcore.business.domain.enums.RuleField;
import com.shipcore.business.domain.enums.RuleOperator;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Entity
@Table(name = "shipping_rules")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class ShippingRule extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private RuleField field;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private RuleOperator operator;

    @Column(nullable = false, length = 120)
    private String value;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private RuleAction action;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal actionValue;

    @Column(nullable = false)
    private Integer priority = 0;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "organization_id", nullable = false)
    private Organization organization;

}
