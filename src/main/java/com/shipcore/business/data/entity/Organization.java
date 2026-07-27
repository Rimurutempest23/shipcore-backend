package com.shipcore.business.data.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.BatchSize;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "organizations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Organization extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(length = 20, unique = true)
    private String ruc;

    @Column(length = 255)
    private String address;

    @Column(length = 20)
    private String phone;

    @Column(nullable = false, length = 2)
    private String country = "PE";

    @Column(nullable = false, length = 30)
    private String plan = "starter";

    @Column(nullable = false)
    private Integer softLimit = 1000;

    @Column(nullable = false)
    private Integer hardLimit = 1200;

    @Column(nullable = false)
    private Integer currentUsage = 0;

    @OneToMany(mappedBy = "organization", fetch = FetchType.LAZY)
    @BatchSize(size = 20)
    private List<User> users = new ArrayList<>();

    @OneToMany(mappedBy = "organization", fetch = FetchType.LAZY)
    @BatchSize(size = 20)
    private List<Carrier> carriers = new ArrayList<>();

    @OneToMany(mappedBy = "organization", fetch = FetchType.LAZY)
    @BatchSize(size = 20)
    private List<Quote> quotes = new ArrayList<>();

    @OneToMany(mappedBy = "organization", fetch = FetchType.LAZY)
    @BatchSize(size = 20)
    private List<ShippingRule> shippingRules = new ArrayList<>();

    @OneToMany(mappedBy = "organization", fetch = FetchType.LAZY)
    @BatchSize(size = 20)
    private List<ApiKey> apiKeys = new ArrayList<>();

}
