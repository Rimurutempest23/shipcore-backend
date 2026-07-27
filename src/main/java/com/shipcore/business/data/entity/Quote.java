package com.shipcore.business.data.entity;

import com.shipcore.business.domain.enums.QuoteStatus;
import com.shipcore.business.domain.enums.ServiceType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Entity
@Table(name = "quotes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Quote extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal packageWeightKg;

    @Column(length = 255)
    private String origin;

    @Column(length = 255)
    private String destination;

    @Column(length = 60)
    private String originZone;

    @Column(length = 60)
    private String destZone;

    @Column(precision = 10, scale = 2)
    private BigDecimal distanceKm;

    @Column(precision = 10, scale = 2)
    private BigDecimal lengthCm;

    @Column(precision = 10, scale = 2)
    private BigDecimal widthCm;

    @Column(precision = 10, scale = 2)
    private BigDecimal heightCm;

    @Enumerated(EnumType.STRING)
    @Column(length = 30)
    private ServiceType serviceType;

    @Column(nullable = false, length = 30)
    private String status;

    @Enumerated(EnumType.STRING)
    @Column(length = 30)
    private QuoteStatus quoteStatus;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "organization_id", nullable = false)
    private Organization organization;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "created_by_user_id", nullable = false)
    private User createdBy;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, optional = false)
    @JoinColumn(name = "origin_address_id", nullable = false)
    private OriginAddress originAddress;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, optional = false)
    @JoinColumn(name = "destination_address_id", nullable = false)
    private DestinationAddress destinationAddress;

    @OneToMany(mappedBy = "quote", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private java.util.List<QuoteResult> results = new java.util.ArrayList<>();

}
