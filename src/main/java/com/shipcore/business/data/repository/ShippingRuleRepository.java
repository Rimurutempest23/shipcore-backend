package com.shipcore.business.data.repository;

import com.shipcore.business.data.entity.ShippingRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ShippingRuleRepository extends JpaRepository<ShippingRule, Long> {

    @Query("""
            select r
            from ShippingRule r
            where r.organization.id = :organizationId
              and r.active = true
            order by r.priority desc, r.id asc
            """)
    List<ShippingRule> findAllActiveByOrganizationId(@Param("organizationId") Long organizationId);

    @Query("""
            select r
            from ShippingRule r
            where r.id = :id
              and r.organization.id = :organizationId
              and r.active = true
            """)
    Optional<ShippingRule> findByIdAndOrganizationId(@Param("id") Long id, @Param("organizationId") Long organizationId);

}
