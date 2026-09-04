package com.acme.trading.portfolio.client;

import com.acme.trading.dto.Quote;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;

/**
 * In-memory stub for market data quotes. In production this would use RestClient
 * against the market-data-service at the configured base URL.
 */
@Component
public class MarketDataClient {

    private static final Map<String, Quote> QUOTES = Map.of(
            "AAPL", new Quote("AAPL", bd("189.50"), bd("189.55"), bd("189.52"), bd("45000000"), Instant.now()),
            "MSFT", new Quote("MSFT", bd("415.20"), bd("415.30"), bd("415.25"), bd("22000000"), Instant.now()),
            "GOOG", new Quote("GOOG", bd("175.80"), bd("175.90"), bd("175.85"), bd("18000000"), Instant.now())
    );

    public Quote getQuote(String symbol) {
        Quote quote = QUOTES.get(symbol.toUpperCase());
        if (quote == null) {
            return new Quote(symbol, bd("100"), bd("100.10"), bd("100.05"), BigDecimal.ZERO, Instant.now());
        }
        return new Quote(quote.symbol(), quote.bid(), quote.ask(), quote.last(), quote.volume(), Instant.now());
    }

    private static BigDecimal bd(String value) {
        return new BigDecimal(value);
    }
}
