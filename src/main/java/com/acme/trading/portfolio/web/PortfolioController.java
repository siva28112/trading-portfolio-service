package com.acme.trading.portfolio.web;

import com.acme.trading.dto.PortfolioSummary;
import com.acme.trading.dto.Position;
import com.acme.trading.portfolio.model.PnlReport;
import com.acme.trading.portfolio.model.RebalanceRecommendation;
import com.acme.trading.portfolio.model.RiskExposure;
import com.acme.trading.portfolio.service.PortfolioService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/portfolio")
public class PortfolioController {

    private final PortfolioService portfolioService;

    public PortfolioController(PortfolioService portfolioService) {
        this.portfolioService = portfolioService;
    }

    @GetMapping("/{accountId}/summary")
    public PortfolioSummary getSummary(@PathVariable String accountId) {
        return portfolioService.getSummary(accountId);
    }

    @GetMapping("/{accountId}/positions")
    public List<Position> getPositions(@PathVariable String accountId) {
        return portfolioService.getPositions(accountId);
    }

    @GetMapping("/{accountId}/pnl")
    public PnlReport getPnl(@PathVariable String accountId) {
        return portfolioService.getPnl(accountId);
    }

    @PostMapping("/{accountId}/rebalance")
    public RebalanceRecommendation rebalance(@PathVariable String accountId) {
        return portfolioService.rebalance(accountId);
    }

    @GetMapping("/{accountId}/risk")
    public RiskExposure getRiskExposure(@PathVariable String accountId) {
        return portfolioService.getRiskExposure(accountId);
    }
}
