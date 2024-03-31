package com.interview.waiwingchoyProtfolioMonitor.bean;

import com.interview.waiwingchoyProtfolioMonitor.enums.SecurityType;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;

@Entity(name="SECURITY_DEFINITION")
@NoArgsConstructor
@Table(name="SECURITY_DEFINITION")
public class SecurityDefinition {

    @Id
    @Column(name="SYMBOL")
    String symbol;

    @Column (name="ROOT_SYMBOL")
    String rootSymbol;

    @Column (name="TYPE")
    @Enumerated(EnumType.STRING)
    SecurityType type;

    @Column (name="STRIKE")
    double strike;

    @Column (name="MATURITY")
    double maturity;

    @Column (name="RISK_FREE_INTEREST")
    double riskFreeInterest;

    @Column (name="VOLATILITY")
    double volatility;

    @Column (name="MU")
    double mu;

    @Column (name="OPEN")
    double open;

    public String getRootSymbol() {
        return rootSymbol;
    }
    public String getSymbol() {
        return symbol;
    }

    public SecurityType getType() {
        return type;
    }

    public double getStrike() {
        return strike;
    }

    public double getMaturity() {
        return maturity;
    }

    public double getRiskFreeInterest() {
        return riskFreeInterest;
    }

    public void setRootSymbol(String rootSymbol) {
        this.rootSymbol = rootSymbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public void setType(SecurityType type) {
        this.type = type;
    }

    public void setStrike(double strike) {
        this.strike = strike;
    }

    public void setMaturity(double maturity) {
        this.maturity = maturity;
    }

    public void setRiskFreeInterest(double riskFreeInterest) {
        this.riskFreeInterest = riskFreeInterest;
    }

    public double getVolatility() {
        return volatility;
    }

    public void setVolatility(double volatility) {
        this.volatility = volatility;
    }

    public double getMu() {
        return mu;
    }

    public void setMu(double mu) {
        this.mu = mu;
    }

    public double getOpen() {
        return open;
    }

    public void setOpen(double open) {
        this.open = open;
    }

    public String toString() {
        return symbol+"|"+rootSymbol+"|"+type+"|"+strike+"|"+maturity+"|"+riskFreeInterest+"|"+volatility+"|"+mu;
    }
}
