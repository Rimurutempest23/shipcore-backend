package com.shipcore.business.data.repository;

import com.shipcore.business.data.entity.CarrierRate;
import com.shipcore.business.domain.enums.RateStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface CarrierRateRepository extends JpaRepository<CarrierRate, Long> {

    @Query("""
            select r
            from CarrierRate r
            join fetch r.carrier c
            join fetch r.organization o
            where r.active = true
            order by c.name, r.zone, r.versionNumber desc
            """)
    List<CarrierRate> findAllActiveWithCarrier();

    @Query("""
            select r
            from CarrierRate r
            join fetch r.carrier c
            join fetch r.organization o
            where r.id = :id
            """)
    Optional<CarrierRate> findByIdWithCarrier(@Param("id") Long id);

    @Query("""
            select r
            from CarrierRate r
            join fetch r.carrier c
            where r.organization.id = :organizationId
              and r.zone = :zone
              and r.status = :status
              and r.active = true
              and :quoteDate between r.validFrom and r.validTo
              and :weightKg between r.minWeightKg and r.maxWeightKg
            order by r.basePrice asc
            """)
    List<CarrierRate> findApplicableRates(
            @Param("organizationId") Long organizationId,
            @Param("zone") String zone,
            @Param("status") RateStatus status,
            @Param("quoteDate") LocalDate quoteDate,
            @Param("weightKg") BigDecimal weightKg);

    @Query("""
            select r
            from CarrierRate r
            join fetch r.carrier c
            where c.id = :carrierId
              and r.zone = :zone
            order by r.versionNumber desc
            """)
    List<CarrierRate> findVersionsByCarrierAndZone(
            @Param("carrierId") Long carrierId,
            @Param("zone") String zone);

}
