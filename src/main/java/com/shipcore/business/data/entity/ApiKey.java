package com.shipcore.business.data.entity;

import com.shipcore.business.domain.enums.ApiEnv;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Entity
@Table(name = "api_keys")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class ApiKey extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ApiEnv environment;

    @Column(nullable = false, length = 40)
    private String keyPreview;

    @Column(nullable = false, length = 255)
    private String keyHash;

    @Column(nullable = false)
    private Integer quotaLimit = 1000;

    @Column(nullable = false)
    private Integer usageCount = 0;

    private LocalDateTime lastUsedAt;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "organization_id", nullable = false)
    private Organization organization;

}
