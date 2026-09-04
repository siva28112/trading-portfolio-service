package com.acme.trading.portfolio.model;

import java.math.BigDecimal;
import java.time.Instant;

public record PnlReport(
        String accountId,
        BigDecimal unrealizedPnl,
        BigDecimal realizedPnl,
        BigDecimal totalPnl,
        Instant asOf
) {}
