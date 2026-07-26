package com.shipcore.organization.repository;

import com.shipcore.organization.entity.Organization;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrganizationRepository extends JpaRepository<Organization, Long> {

    Optional<Organization> findByRuc(String ruc);// buscar un ruc

    boolean existsByRuc(String ruc);//verificar si ya existe

}