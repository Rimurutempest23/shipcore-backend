package com.shipcore.business.data.entity;

import com.shipcore.business.domain.enums.RateStatus;
import com.shipcore.business.domain.enums.RateSource;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(
        name = "carrier_rates",
        indexes = {
                @Index(name = "idx_rate_lookup", columnList = "organization_id, zone, status, valid_from, valid_to"),
                @Index(name = "idx_rate_carrier_zone", columnList = "carrier_id, zone")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class CarrierRate extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 60)
    private String zone;

    @Column(nullable = false, length = 60)
    private String serviceType = "standard";

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal minWeightKg;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal maxWeightKg;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal basePrice;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal pricePerKg;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal pricePerKm = BigDecimal.ZERO;

    @Column(nullable = false)
    private Integer transitDaysMin = 1;

    @Column(nullable = false)
    private Integer transitDaysMax = 1;

    @Column(nullable = false)
    private LocalDate validFrom;

    @Column(nullable = false)
    private LocalDate validTo;

    @Column(nullable = false)
    private Integer versionNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RateStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RateSource source = RateSource.MANUAL;

    @Version
    private Long lockVersion;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "carrier_id", nullable = false)
    private Carrier carrier;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "organization_id", nullable = false)
    private Organization organization;

}
