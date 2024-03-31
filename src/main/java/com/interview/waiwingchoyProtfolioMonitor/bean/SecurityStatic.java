package com.interview.waiwingchoyProtfolioMonitor.bean;

import com.interview.waiwingchoyProtfolioMonitor.calculator.PriceCalculator;

public record SecurityStatic(SecurityDefinition securityDefinition, PriceCalculator priceCalculator, int positionSize) {
}
