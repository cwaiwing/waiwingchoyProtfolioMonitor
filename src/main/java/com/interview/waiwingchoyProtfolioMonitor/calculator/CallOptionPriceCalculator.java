package com.interview.waiwingchoyProtfolioMonitor.calculator;

import com.interview.waiwingchoyProtfolioMonitor.BlackScholesCache;
import com.interview.waiwingchoyProtfolioMonitor.bean.SecurityDefinition;

public class CallOptionPriceCalculator extends PriceCalculator {
    @Override
    public double calculatePrice(double price, SecurityDefinition securityDefinition) {
        double S = price;
        double X = securityDefinition.getStrike();
        double r = securityDefinition.getRiskFreeInterest();
        double T = securityDefinition.getMaturity();
        double sigma = securityDefinition.getVolatility();
        double d1 = (Math.log(S / X) + (r + (sigma * sigma) / 2) * T) / (sigma * Math.sqrt(T));
        double d2 = d1 - sigma * Math.sqrt(T);
        double ret = (S * N(d1)) - X * Math.exp(-r * T) * N(d2);
        return ret;
    }

    @Override
    public double calByCache(double price, BlackScholesCache cache) {

        double d1=Math.log(price/cache.getX()) / (cache.getSigmaXsqrtT()) + cache.getD1Part2();
        double d2=d1-cache.getSigmaXsqrtT();

        double formularPart1=price * N(d1);
        double formularPart2=cache.getKert() * N(d2);
        return Math.floor((formularPart1 - formularPart2) *100) / 100;
    }
}
