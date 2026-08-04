package com.shipcore.business.domain.service.impl;

import com.shipcore.business.api.dto.response.DashboardSummaryResponse;
import com.shipcore.business.api.exception.ResourceNotFoundException;
import com.shipcore.business.data.entity.Organization;
import com.shipcore.business.data.entity.User;
import com.shipcore.business.data.repository.QuoteRepository;
import com.shipcore.business.data.repository.UserRepository;
import com.shipcore.business.domain.service.DashboardService;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardServiceImpl implements DashboardService {

    private final UserRepository userRepository;
    private final QuoteRepository quoteRepository;
    private final EntityManager entityManager;

    @Override
    public DashboardSummaryResponse getSummary(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado."));

        Organization org = user.getOrganization();

        // Standard JPQL aggregations
        long totalQuotes = quoteRepository.countByOrganizationId(org.getId());

        LocalDateTime startOfMonth = LocalDateTime.now().with(TemporalAdjusters.firstDayOfMonth()).withHour(0).withMinute(0).withSecond(0);
        long monthlyQuotes = quoteRepository.countByOrganizationIdAndCreatedAtAfter(org.getId(), startOfMonth);

        // Calculate estimated savings (15% of selected quote prices) via JPQL
        Object rawSavings = entityManager.createQuery("""
                select sum(r.price * 0.15)
                from QuoteResult r
                join r.quote q
                where q.organization.id = :orgId
                  and r.selected = true
                  and q.active = true
                """)
                .setParameter("orgId", org.getId())
                .getSingleResult();
        BigDecimal estimatedSavings = rawSavings != null
                ? new BigDecimal(rawSavings.toString())
                : BigDecimal.ZERO;

        return new DashboardSummaryResponse(
                totalQuotes,
                monthlyQuotes,
                estimatedSavings,
                org.getCurrentUsage(),
                org.getSoftLimit(),
                org.getHardLimit()
        );
    }

}
