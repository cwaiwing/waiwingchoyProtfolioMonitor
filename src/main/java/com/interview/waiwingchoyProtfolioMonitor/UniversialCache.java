package com.interview.waiwingchoyProtfolioMonitor;

import com.interview.waiwingchoyProtfolioMonitor.bean.SecurityDefinition;
import com.interview.waiwingchoyProtfolioMonitor.bean.SecurityStatic;
import com.interview.waiwingchoyProtfolioMonitor.calculator.PriceCalculator;
import org.springframework.javapoet.AnnotationSpec;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class UniversialCache {
    private final Map<String, SecurityDefinition> securityDefinitionMap;
    private final static Object lock = new Object();
    private static UniversialCache universialCache;
    private final Map<String, PriceCalculator> priceCalculatorMap;
    private final Map<String, BlackScholesCache> blackScholesCacheMap;

    public static UniversialCache getInstance() {
        UniversialCache result = universialCache;
        if (result == null) {
            synchronized (lock) {
                result = universialCache;
                if (result == null)
                    universialCache = result = new UniversialCache();
            }
        }
        return result;
    }
    private UniversialCache() {
        securityDefinitionMap = new ConcurrentHashMap<>();
        priceCalculatorMap = new ConcurrentHashMap<>();
        blackScholesCacheMap = new ConcurrentHashMap<>();
    }

    public SecurityDefinition getSecurityDefinition(String symbol) {
        return securityDefinitionMap.get(symbol);
    }

    public void setSecurityDefinition(String symbol, SecurityDefinition securityDefinition) {
        securityDefinitionMap.put(symbol, securityDefinition);
    }

    public PriceCalculator getPriceCalculator(String symbol) {
        return priceCalculatorMap.get(symbol);
    }
    public void setPriceCalculator(String symbol, PriceCalculator priceCalculator) {
        priceCalculatorMap.put(symbol, priceCalculator);
    }

    public BlackScholesCache getBlackScholesCache(String symbol) {
        return blackScholesCacheMap.get(symbol);
    }
    public void setBlackScholesCache(String symbol, BlackScholesCache cache) {
        blackScholesCacheMap.put(symbol, cache);
    }
}
