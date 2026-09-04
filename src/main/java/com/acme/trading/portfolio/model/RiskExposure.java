package com.acme.trading.portfolio.model;

import java.math.BigDecimal;
import java.util.Map;

public record RiskExposure(
        String accountId,
        BigDecimal grossExposure,
        BigDecimal netExposure,
        BigDecimal leverageRatio,
        Map<String, BigDecimal> sectorExposure
) {}
