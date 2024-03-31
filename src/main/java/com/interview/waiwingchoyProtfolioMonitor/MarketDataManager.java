package com.interview.waiwingchoyProtfolioMonitor;

import com.interview.waiwingchoyProtfolioMonitor.bean.SecurityDefinition;
import com.interview.waiwingchoyProtfolioMonitor.runnable.MarketDataProvider;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;

import java.util.concurrent.ConcurrentHashMap;
// Holding all securities feed provider
public class MarketDataManager {
    private static MarketDataManager marketDataManager = null;
    private static final Object lock = new Object();
    private final ConcurrentHashMap<String, MarketDataProvider> marketDataProviderMap = new ConcurrentHashMap<>();
    private TaskExecutor executor = new SimpleAsyncTaskExecutor();

    private MarketDataManager() {}
    public static MarketDataManager getInstance() {
        MarketDataManager result = marketDataManager;
        if (result == null) {
            synchronized (lock) {
                result = marketDataManager;
                if (result == null)
                    marketDataManager = result = new MarketDataManager();
            }
        }
        return result;
    }

    public void startMarketDataProvider() {
        for (String key : marketDataProviderMap.keySet()) {
            executor.execute(marketDataProviderMap.get(key));
        }
    }

    public void subscribeMarketData(String symbol, TraderPortfilio traderPortfilio, SecurityDefinition securityDefinition) {
        MarketDataProvider marketDataProvider = new MarketDataProvider(symbol, traderPortfilio, securityDefinition.getMu(), securityDefinition.getVolatility(), securityDefinition.getOpen());
        marketDataProviderMap.put(symbol, marketDataProvider);
        System.out.println("Subscribe marketdata: "+symbol);
    }
}
