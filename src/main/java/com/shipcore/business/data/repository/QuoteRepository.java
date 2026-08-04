package com.shipcore.business.data.repository;

import com.shipcore.business.data.entity.Quote;
import com.shipcore.business.domain.enums.QuoteStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;

public interface QuoteRepository extends JpaRepository<Quote, Long> {

    @Query("""
            select distinct q
            from Quote q
            join fetch q.createdBy u
            left join fetch q.results r
            left join fetch r.carrier c
            where q.organization.id = :organizationId
              and (:userId is null or u.id = :userId)
              and q.active = true
              and (:statusFilterEnabled = false or q.quoteStatus in :statuses)
              and (:dateFrom is null or q.createdAt >= :dateFrom)
              and (:dateTo is null or q.createdAt <= :dateTo)
              and (:carrierId is null or r.carrier.id = :carrierId)
              and (:search is null or LOWER(q.origin) like LOWER(CONCAT('%', :search, '%')) or LOWER(q.destination) like LOWER(CONCAT('%', :search, '%')))
            order by q.createdAt desc
            """)
    Page<Quote> findFilteredQuotes(
            @Param("organizationId") Long organizationId,
            @Param("userId") Long userId,
            @Param("statusFilterEnabled") boolean statusFilterEnabled,
            @Param("statuses") Collection<QuoteStatus> statuses,
            @Param("dateFrom") LocalDateTime dateFrom,
            @Param("dateTo") LocalDateTime dateTo,
            @Param("carrierId") Long carrierId,
            @Param("search") String search,
            Pageable pageable);

    @Modifying
    @Query("""
            update Quote q
            set q.quoteStatus = com.shipcore.business.domain.enums.QuoteStatus.EXPIRED,
                q.status = 'EXPIRED'
            where q.organization.id = :organizationId
              and q.active = true
              and q.quoteStatus in :openStatuses
              and q.createdAt < :cutoff
            """)
    int expireOpenQuotes(
            @Param("organizationId") Long organizationId,
            @Param("openStatuses") Collection<QuoteStatus> openStatuses,
            @Param("cutoff") LocalDateTime cutoff);

    @Query("""
            select q
            from Quote q
            join fetch q.createdBy u
            left join fetch q.results r
            left join fetch r.carrier c
            where q.id = :id
              and q.organization.id = :organizationId
              and q.active = true
            """)
    Optional<Quote> findByIdAndOrganizationIdWithDetails(@Param("id") Long id, @Param("organizationId") Long organizationId);

    @Query("select count(q) from Quote q where q.organization.id = :organizationId and q.active = true")
    long countByOrganizationId(@Param("organizationId") Long organizationId);

    @Query("select count(q) from Quote q where q.organization.id = :organizationId and q.active = true and q.createdAt >= :startDate")
    long countByOrganizationIdAndCreatedAtAfter(@Param("organizationId") Long organizationId, @Param("startDate") LocalDateTime startDate);
}
