package com.shipcore.business.data.repository;

import com.shipcore.business.data.entity.Carrier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CarrierRepository extends JpaRepository<Carrier, Long> {

    boolean existsByOrganizationIdAndNameIgnoreCase(Long organizationId, String name);

    @Query("""
            select c
            from Carrier c
            join fetch c.organization o
            where c.active = true
            order by c.name
            """)
    List<Carrier> findAllActiveWithOrganization();

    @Query("""
            select c
            from Carrier c
            join fetch c.organization o
            where c.id = :id
            """)
    Optional<Carrier> findByIdWithOrganization(@Param("id") Long id);

}
