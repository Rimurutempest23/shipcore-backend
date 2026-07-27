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

    @OneToMany(mappedBy = "organization", fetch = FetchType.LAZY)
    @BatchSize(size = 20)
    private List<User> users = new ArrayList<>();

    @OneToMany(mappedBy = "organization", fetch = FetchType.LAZY)
    @BatchSize(size = 20)
    private List<Carrier> carriers = new ArrayList<>();

    @OneToMany(mappedBy = "organization", fetch = FetchType.LAZY)
    @BatchSize(size = 20)
    private List<Quote> quotes = new ArrayList<>();

}
