package com.acme.trading.portfolio.service;

import com.acme.trading.dto.PortfolioSummary;
import com.acme.trading.dto.Position;
import com.acme.trading.dto.Quote;
import com.acme.trading.portfolio.client.MarketDataClient;
import com.acme.trading.portfolio.model.PnlReport;
import com.acme.trading.portfolio.model.RebalanceRecommendation;
import com.acme.trading.portfolio.model.RiskExposure;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class PortfolioService {

    private static final Map<String, List<SampleHolding>> SAMPLE_PORTFOLIOS = Map.of(
            "ACC-001", List.of(
                    new SampleHolding("AAPL", bd("150"), bd("170.00")),
                    new SampleHolding("MSFT", bd("80"), bd("380.00")),
                    new SampleHolding("GOOG", bd("50"), bd("160.00"))
            )
    );

    private final MarketDataClient marketDataClient;

    public PortfolioService(MarketDataClient marketDataClient) {
        this.marketDataClient = marketDataClient;
    }

    public PortfolioSummary getSummary(String accountId) {
        List<Position> positions = getPositions(accountId);
        BigDecimal totalMarketValue = positions.stream()
                .map(Position::marketValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalUnrealized = positions.stream()
                .map(Position::unrealizedPnl)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new PortfolioSummary(
                accountId,
                totalMarketValue,
                totalUnrealized,
                bd("1250.00"),
                bd("50000.00"),
                positions,
                Instant.now()
        );
    }

    public List<Position> getPositions(String accountId) {
        List<SampleHolding> holdings = SAMPLE_PORTFOLIOS.getOrDefault(accountId, List.of());
        List<Position> positions = new ArrayList<>();

        for (SampleHolding holding : holdings) {
            Quote quote = marketDataClient.getQuote(holding.symbol());
            BigDecimal marketPrice = quote.last();
            BigDecimal marketValue = marketPrice.multiply(holding.quantity()).setScale(2, RoundingMode.HALF_UP);
            BigDecimal costBasis = holding.averageCost().multiply(holding.quantity());
            BigDecimal unrealized = marketValue.subtract(costBasis).setScale(2, RoundingMode.HALF_UP);

            positions.add(new Position(
                    holding.symbol(),
                    holding.quantity(),
                    holding.averageCost(),
                    marketPrice,
                    marketValue,
                    unrealized
            ));
        }
        return positions;
    }

    public PnlReport getPnl(String accountId) {
        List<Position> positions = getPositions(accountId);
        BigDecimal unrealized = positions.stream()
                .map(Position::unrealizedPnl)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal realized = bd("1250.00");

        return new PnlReport(accountId, unrealized, realized, unrealized.add(realized), Instant.now());
    }

    public RebalanceRecommendation rebalance(String accountId) {
        List<Position> positions = getPositions(accountId);
        BigDecimal total = positions.stream()
                .map(Position::marketValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<RebalanceRecommendation.RebalanceAction> actions = new ArrayList<>();
        BigDecimal targetWeight = bd("33.33");

        for (Position position : positions) {
            BigDecimal currentWeight = position.marketValue()
                    .divide(total, 4, RoundingMode.HALF_UP)
                    .multiply(bd("100"));
            String action = currentWeight.compareTo(targetWeight) > 0 ? "TRIM" : "ADD";
            actions.add(new RebalanceRecommendation.RebalanceAction(
                    position.symbol(), action, targetWeight, currentWeight
            ));
        }

        return new RebalanceRecommendation(
                accountId,
                actions,
                "Equal-weight rebalance toward 33.33% per equity holding"
        );
    }

    public RiskExposure getRiskExposure(String accountId) {
        List<Position> positions = getPositions(accountId);
        BigDecimal gross = positions.stream()
                .map(Position::marketValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal net = gross;
        BigDecimal leverage = gross.divide(bd("50000.00"), 2, RoundingMode.HALF_UP);

        return new RiskExposure(
                accountId,
                gross,
                net,
                leverage,
                Map.of("TECH", gross)
        );
    }

    private record SampleHolding(String symbol, BigDecimal quantity, BigDecimal averageCost) {}

    private static BigDecimal bd(String value) {
        return new BigDecimal(value);
    }
}
