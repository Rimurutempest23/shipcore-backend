package com.shipcore.business.data.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.BatchSize;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "carriers",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_carriers_organization_name",
                columnNames = {"organization_id", "name"}
        )
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Carrier extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String name;

    @Column(nullable = false, length = 60)
    private String serviceType;

    @Column(length = 80)
    private String contactEmail;

    @Column(length = 20)
    private String phone;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "organization_id", nullable = false)
    private Organization organization;

    @OneToMany(mappedBy = "carrier", fetch = FetchType.LAZY)
    @BatchSize(size = 20)
    private List<CarrierRate> rates = new ArrayList<>();

}
