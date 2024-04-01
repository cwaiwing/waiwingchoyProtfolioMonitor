package com.interview.waiwingchoyProtfolioMonitor.calculator;

import com.interview.waiwingchoyProtfolioMonitor.BlackScholesCache;
import com.interview.waiwingchoyProtfolioMonitor.bean.SecurityDefinition;

public abstract class PriceCalculator {

    public abstract double calculatePrice(double price, SecurityDefinition securityDefinition);
    public abstract double calByCache(double price, BlackScholesCache cache) ;

    protected double N(double x) {
        int neg = (x < 0d) ? 1 : 0;
        if ( neg == 1)
            x *= -1d;

        double k = (1d / ( 1d + 0.2316419 * x));
        double y = (((( 1.330274429 * k - 1.821255978) * k + 1.781477937) *
                k - 0.356563782) * k + 0.319381530) * k;
        y = 1.0 - 0.398942280401 * Math.exp(-0.5 * x * x) * y;

        return (1d - neg) * y + neg * (1d - y);
    }
}
