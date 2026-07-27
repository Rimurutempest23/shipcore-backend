package com.shipcore.business.data.repository;

import com.shipcore.business.data.entity.Organization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface OrganizationRepository extends JpaRepository<Organization, Long> {

    Optional<Organization> findByRuc(String ruc);

    boolean existsByRuc(String ruc);

    @Query("""
            select o
            from Organization o
            where o.active = true
            order by o.name
            """)
    List<Organization> findAllActive();

}
