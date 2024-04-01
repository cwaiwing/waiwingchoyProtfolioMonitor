package com.interview.waiwingchoyProtfolioMonitor.calculator;

import com.interview.waiwingchoyProtfolioMonitor.BlackScholesCache;
import com.interview.waiwingchoyProtfolioMonitor.bean.SecurityDefinition;
public class SimplePriceCalculator extends PriceCalculator {
    @Override
    public double calculatePrice(double price, SecurityDefinition securityDefinition) {
        return price;
    }

    @Override
    public double calByCache(double price, BlackScholesCache cache) {
        return price;
    }
}
