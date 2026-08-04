package com.shipcore.business.domain.service.impl;

import com.shipcore.business.api.dto.request.QuoteRequest;
import com.shipcore.business.api.dto.response.PageResponse;
import com.shipcore.business.api.dto.response.QuoteResponse;
import com.shipcore.business.api.exception.BusinessRuleException;
import com.shipcore.business.api.exception.ResourceNotFoundException;
import com.shipcore.business.data.entity.*;
import com.shipcore.business.data.repository.*;
import com.shipcore.business.domain.enums.QuoteStatus;
import com.shipcore.business.domain.enums.RateStatus;
import com.shipcore.business.domain.enums.RuleAction;
import com.shipcore.business.domain.mapper.QuoteMapper;
import com.shipcore.business.domain.service.ApiKeyService;
import com.shipcore.business.domain.service.QuoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class QuoteServiceImpl implements QuoteService {

    private final QuoteRepository quoteRepository;
    private final QuoteResultRepository quoteResultRepository;
    private final CarrierRateRepository carrierRateRepository;
    private final ShippingRuleRepository shippingRuleRepository;
    private final UserRepository userRepository;
    private final OrganizationRepository organizationRepository;
    private final QuoteMapper quoteMapper;
    private final ApiKeyService apiKeyService;
    private static final int QUOTE_EXPIRATION_DAYS = 7;

    @Override
    public QuoteResponse createQuote(QuoteRequest request, String userEmail) {
        User user = findUser(userEmail);
        Organization organization = user.getOrganization();
        resetOrganizationUsageIfDue(organization);

        // 1. Quota Check
        if (organization.getCurrentUsage() >= organization.getHardLimit()) {
            throw new BusinessRuleException("Se ha alcanzado el limite maximo de cotizaciones (Hard Limit) para la organizacion.");
        }

        // 2. Fetch Applicable Rates via JPQL
        List<CarrierRate> applicableRates = carrierRateRepository.findApplicableRates(
                organization.getId(),
                request.destZone(),
                RateStatus.ACTIVE,
                LocalDate.now(),
                request.packageWeightKg()
        );

        if (applicableRates.isEmpty()) {
            throw new BusinessRuleException("No se encontraron tarifas activas aplicables para la zona y peso especificados.");
        }

        // 3. Fetch Active Rules ordered by priority
        List<ShippingRule> activeRules = shippingRuleRepository.findAllActiveByOrganizationId(organization.getId());

        // 4. Build Quote Entity & Addresses
        OriginAddress originAddress = OriginAddress.builder()
                .city(request.origin())
                .district(request.origin())
                .zone(request.originZone())
                .street(request.origin())
                .active(true)
                .build();

        DestinationAddress destinationAddress = DestinationAddress.builder()
                .city(request.destination())
                .district(request.destination())
                .zone(request.destZone())
                .street(request.destination())
                .active(true)
                .build();

        Quote quote = quoteMapper.toEntity(request);
        quote.setOrganization(organization);
        quote.setCreatedBy(user);
        quote.setOriginAddress(originAddress);
        quote.setDestinationAddress(destinationAddress);
        quote.setStatus("COMPLETED");
        quote.setQuoteStatus(QuoteStatus.QUOTED);
        quote.setActive(true);

        Quote savedQuote = quoteRepository.save(quote);

        // 5. Quote Engine Execution (Functional Programming using Java Streams & Lambdas)
        List<QuoteResult> results = applicableRates.stream()
                .map(rate -> calculateResult(savedQuote, rate, request, activeRules))
                .filter(Objects::nonNull) // Filter out BLOCKED rates
                .sorted(Comparator.comparing(QuoteResult::getPrice))
                .collect(Collectors.toList());

        if (results.isEmpty()) {
            throw new BusinessRuleException("Todas las tarifas aplicables fueron bloqueadas por las reglas de negocio.");
        }

        // Batch save QuoteResults
        quoteResultRepository.saveAll(results);

        // Update quote results reference
        savedQuote.setResults(results);

        // Increment Organization Current Usage
        organizationRepository.incrementUsage(organization.getId());

        return quoteMapper.toResponse(savedQuote);
    }

    @Override
    public QuoteResponse createQuoteFromApiKey(QuoteRequest request, ApiKey apiKey) {
        Organization organization = apiKey.getOrganization();
        resetOrganizationUsageIfDue(organization);
        User user = userRepository.findFirstByOrganizationIdAndActiveTrueOrderByIdAsc(organization.getId())
                .orElseThrow(() -> new ResourceNotFoundException("No existe un usuario activo para auditar la cotizacion externa."));

        if (organization.getCurrentUsage() >= organization.getHardLimit()) {
            throw new BusinessRuleException("Se ha alcanzado el limite maximo de cotizaciones (Hard Limit) para la organizacion.");
        }

        List<CarrierRate> applicableRates = carrierRateRepository.findApplicableRates(
                organization.getId(),
                request.destZone(),
                RateStatus.ACTIVE,
                LocalDate.now(),
                request.packageWeightKg()
        );

        if (applicableRates.isEmpty()) {
            throw new BusinessRuleException("No se encontraron tarifas activas aplicables para la zona y peso especificados.");
        }

        List<ShippingRule> activeRules = shippingRuleRepository.findAllActiveByOrganizationId(organization.getId());

        OriginAddress originAddress = OriginAddress.builder()
                .city(request.origin())
                .district(request.origin())
                .zone(request.originZone())
                .street(request.origin())
                .active(true)
                .build();

        DestinationAddress destinationAddress = DestinationAddress.builder()
                .city(request.destination())
                .district(request.destination())
                .zone(request.destZone())
                .street(request.destination())
                .active(true)
                .build();

        Quote quote = quoteMapper.toEntity(request);
        quote.setOrganization(organization);
        quote.setCreatedBy(user);
        quote.setOriginAddress(originAddress);
        quote.setDestinationAddress(destinationAddress);
        quote.setStatus("COMPLETED");
        quote.setQuoteStatus(QuoteStatus.QUOTED);
        quote.setActive(true);

        Quote savedQuote = quoteRepository.save(quote);

        List<QuoteResult> results = applicableRates.stream()
                .map(rate -> calculateResult(savedQuote, rate, request, activeRules))
                .filter(Objects::nonNull)
                .sorted(Comparator.comparing(QuoteResult::getPrice))
                .collect(Collectors.toList());

        if (results.isEmpty()) {
            throw new BusinessRuleException("Todas las tarifas aplicables fueron bloqueadas por las reglas de negocio.");
        }

        quoteResultRepository.saveAll(results);
        savedQuote.setResults(results);

        organizationRepository.incrementUsage(organization.getId());
        apiKeyService.recordUsage(apiKey);

        return quoteMapper.toResponse(savedQuote);
    }

    @Override
    public PageResponse<QuoteResponse> findQuotes(
            String userEmail,
            String status,
            LocalDateTime dateFrom,
            LocalDateTime dateTo,
            Long carrierId,
            String search,
            Pageable pageable) {

        User user = findUser(userEmail);
        expireOpenQuotes(user.getOrganization().getId());
        Long filterUserId = user.getRole().name().contains("ADMIN") ? null : user.getId();
        List<QuoteStatus> normalizedStatuses = normalizeStatusFilter(status);
        List<QuoteStatus> queryStatuses = normalizedStatuses != null
                ? normalizedStatuses
                : List.of(QuoteStatus.QUOTED, QuoteStatus.DRAFT, QuoteStatus.BOOKED, QuoteStatus.ACCEPTED, QuoteStatus.EXPIRED);
        Page<Quote> quotePage = quoteRepository.findFilteredQuotes(
                user.getOrganization().getId(),
                filterUserId,
                normalizedStatuses != null,
                queryStatuses,
                dateFrom,
                dateTo,
                carrierId,
                search,
                pageable
        );

        return PageResponse.of(quotePage.map(quoteMapper::toResponse));
    }

    @Override
    public QuoteResponse findById(Long id, String userEmail) {
        User user = findUser(userEmail);
        expireOpenQuotes(user.getOrganization().getId());
        Quote quote = quoteRepository.findByIdAndOrganizationIdWithDetails(id, user.getOrganization().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Cotizacion no encontrada."));
        return quoteMapper.toResponse(quote);
    }

    @Override
    public QuoteResponse selectQuoteResult(Long quoteId, Long resultId, String userEmail) {
        User user = findUser(userEmail);
        expireOpenQuotes(user.getOrganization().getId());
        Quote quote = quoteRepository.findByIdAndOrganizationIdWithDetails(quoteId, user.getOrganization().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Cotizacion no encontrada."));

        if (quote.getQuoteStatus() == QuoteStatus.EXPIRED) {
            throw new BusinessRuleException("La cotizacion esta expirada y no puede reservarse.");
        }

        QuoteResult selectedResult = quoteResultRepository.findByIdAndQuoteIdAndOrganizationId(resultId, quoteId, user.getOrganization().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Resultado de cotizacion no encontrado."));

        // Unselect all existing results
        quote.getResults().forEach(r -> r.setSelected(false));

        // Select the chosen result
        selectedResult.setSelected(true);
        quote.setQuoteStatus(QuoteStatus.BOOKED);
        quote.setStatus("BOOKED");

        quoteRepository.save(quote);
        return quoteMapper.toResponse(quote);
    }

    /**
     * Motor de calculo de tarifa con evaluacion dinamica de reglas mediante Lambdas y Streams.
     */
    private QuoteResult calculateResult(Quote quote, CarrierRate rate, QuoteRequest request, List<ShippingRule> rules) {
        // Base cost calculation: basePrice + (weight * pricePerKg) + (distance * pricePerKm)
        BigDecimal weightCost = request.packageWeightKg().multiply(rate.getPricePerKg());
        BigDecimal distanceKm = request.distanceKm() != null ? request.distanceKm() : BigDecimal.ZERO;
        BigDecimal distanceCost = distanceKm.multiply(rate.getPricePerKm());

        BigDecimal finalPrice = rate.getBasePrice().add(weightCost).add(distanceCost);

        boolean blocked = false;
        boolean preferred = false;
        List<String> restrictionNotes = new ArrayList<>();

        // Process Rules Functional Pipeline
        for (ShippingRule rule : rules) {
            if (evaluateRuleCondition(rule, rate, request)) {
                if (rule.getAction() == RuleAction.BLOCK) {
                    blocked = true;
                    break;
                } else if (rule.getAction() == RuleAction.SURCHARGE) {
                    BigDecimal surchargeMultiplier = BigDecimal.ONE.add(rule.getActionValue().divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP));
                    finalPrice = finalPrice.multiply(surchargeMultiplier);
                    restrictionNotes.add("Recargo de " + rule.getActionValue() + "% aplicado por regla: " + rule.getName());
                } else if (rule.getAction() == RuleAction.DISCOUNT) {
                    BigDecimal discountMultiplier = BigDecimal.ONE.subtract(rule.getActionValue().divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP));
                    finalPrice = finalPrice.multiply(discountMultiplier);
                    restrictionNotes.add("Descuento de " + rule.getActionValue() + "% aplicado por regla: " + rule.getName());
                } else if (rule.getAction() == RuleAction.PREFER) {
                    preferred = true;
                    restrictionNotes.add("Courier preferido por regla: " + rule.getName());
                }
            }
        }

        if (blocked) {
            return null; // Expressed as null to be filtered out by stream
        }

        return QuoteResult.builder()
                .quote(quote)
                .carrier(rate.getCarrier())
                .carrierRate(rate)
                .rateVersionUsed(rate.getVersionNumber())
                .price(finalPrice.setScale(2, RoundingMode.HALF_UP))
                .transitDaysMin(rate.getTransitDaysMin())
                .transitDaysMax(rate.getTransitDaysMax())
                .restrictions(String.join("; ", restrictionNotes))
                .selected(false)
                .preferred(preferred)
                .active(true)
                .build();
    }

    /**
     * Evaluador dinamico de condiciones de reglas mediante Lambdas y evaluacion de campo/operador.
     */
    private boolean evaluateRuleCondition(ShippingRule rule, CarrierRate rate, QuoteRequest request) {
        String ruleValue = rule.getValue();

        return switch (rule.getField()) {
            case WEIGHT -> evaluateNumeric(request.packageWeightKg(), rule.getOperator(), ruleValue);
            case ZONE -> evaluateString(request.destZone(), rule.getOperator(), ruleValue);
            case SERVICE_TYPE -> evaluateString(rate.getServiceType(), rule.getOperator(), ruleValue);
            case CARRIER -> evaluateString(rate.getCarrier().getName(), rule.getOperator(), ruleValue);
        };
    }

    private boolean evaluateNumeric(BigDecimal actualValue, com.shipcore.business.domain.enums.RuleOperator operator, String ruleValue) {
        try {
            BigDecimal targetValue = new BigDecimal(ruleValue);
            int comp = actualValue.compareTo(targetValue);
            return switch (operator) {
                case GT -> comp > 0;
                case GTE -> comp >= 0;
                case LT -> comp < 0;
                case LTE -> comp <= 0;
                case EQ -> comp == 0;
                case NEQ -> comp != 0;
                default -> false;
            };
        } catch (Exception e) {
            return false;
        }
    }

    private boolean evaluateString(String actualValue, com.shipcore.business.domain.enums.RuleOperator operator, String ruleValue) {
        if (actualValue == null) return false;
        return switch (operator) {
            case EQ -> actualValue.equalsIgnoreCase(ruleValue);
            case NEQ -> !actualValue.equalsIgnoreCase(ruleValue);
            case CONTAINS -> actualValue.toLowerCase().contains(ruleValue.toLowerCase());
            default -> false;
        };
    }

    private User findUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado."));
    }

    private List<QuoteStatus> normalizeStatusFilter(String status) {
        if (status == null || status.isBlank()) {
            return null;
        }

        return switch (status.toUpperCase()) {
            case "BOOKED", "RESERVED", "RESERVADA" -> List.of(QuoteStatus.BOOKED, QuoteStatus.ACCEPTED);
            case "EXPIRED", "EXPIRADA" -> List.of(QuoteStatus.EXPIRED);
            default -> List.of(QuoteStatus.QUOTED, QuoteStatus.DRAFT);
        };
    }

    private void resetOrganizationUsageIfDue(Organization organization) {
        LocalDateTime lastReset = organization.getUpdatedAt();
        if (lastReset == null) {
            organizationRepository.save(organization);
            return;
        }

        LocalDateTime nextReset = switch (organization.getPlan().toLowerCase()) {
            case "enterprise" -> lastReset.plusDays(1);
            case "growth" -> lastReset.plusWeeks(1);
            default -> lastReset.plusMonths(1);
        };

        if (LocalDateTime.now().isBefore(nextReset)) {
            return;
        }

        organization.setCurrentUsage(0);
        organizationRepository.save(organization);
    }

    private void expireOpenQuotes(Long organizationId) {
        LocalDateTime cutoff = LocalDateTime.now().minusDays(QUOTE_EXPIRATION_DAYS);
        quoteRepository.expireOpenQuotes(
                organizationId,
                List.of(QuoteStatus.QUOTED, QuoteStatus.DRAFT),
                cutoff
        );
    }

}
