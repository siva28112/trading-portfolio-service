package com.acme.trading.portfolio.model;

import java.math.BigDecimal;
import java.util.List;

public record RebalanceRecommendation(
        String accountId,
        List<RebalanceAction> actions,
        String rationale
) {
    public record RebalanceAction(
            String symbol,
            String action,
            BigDecimal targetWeight,
            BigDecimal currentWeight
    ) {}
}
