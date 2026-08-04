package com.shipcore.business.data.repository;

import com.shipcore.business.data.entity.ApiKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ApiKeyRepository extends JpaRepository<ApiKey, Long> {

    @Query("""
            select k
            from ApiKey k
            where k.organization.id = :organizationId
              and k.active = true
            order by k.createdAt desc
            """)
    List<ApiKey> findAllActiveByOrganizationId(@Param("organizationId") Long organizationId);

    @Query("""
            select k
            from ApiKey k
            where k.id = :id
              and k.organization.id = :organizationId
              and k.active = true
            """)
    Optional<ApiKey> findByIdAndOrganizationId(@Param("id") Long id, @Param("organizationId") Long organizationId);

    @Query("""
            select k
            from ApiKey k
            join fetch k.organization
            where k.keyPreview = :keyPreview
              and k.active = true
            """)
    Optional<ApiKey> findByKeyPreviewAndActiveTrue(@Param("keyPreview") String keyPreview);

}
