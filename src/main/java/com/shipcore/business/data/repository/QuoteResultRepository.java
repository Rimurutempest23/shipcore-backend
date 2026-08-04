package com.shipcore.business.data.repository;

import com.shipcore.business.data.entity.QuoteResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface QuoteResultRepository extends JpaRepository<QuoteResult, Long> {

    List<QuoteResult> findByQuoteId(Long quoteId);

    @Query("""
            select r
            from QuoteResult r
            where r.id = :id
              and r.quote.id = :quoteId
              and r.quote.organization.id = :organizationId
            """)
    Optional<QuoteResult> findByIdAndQuoteIdAndOrganizationId(
            @Param("id") Long id,
            @Param("quoteId") Long quoteId,
            @Param("organizationId") Long organizationId);

}
