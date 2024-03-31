package com.interview.waiwingchoyProtfolioMonitor.runnable;

import com.interview.waiwingchoyProtfolioMonitor.Tick;
import com.interview.waiwingchoyProtfolioMonitor.implement.TickReceivable;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class MarketDataProvider implements Runnable{
    private final String symbol;
    private final TickReceivable tickReceivable;
    private final double mu;
    private final double volatility;
    private final double minWaitSec = 0.5;
    private final double maxWaitSec = 2d;
    private final double open;
    private final AtomicLong cnt = new AtomicLong(0);

    public MarketDataProvider(String symbol, TickReceivable tickReceivable, double mu, double volatility, double open) {
        this.symbol = symbol;
        this.tickReceivable = tickReceivable;
        this.mu = mu;
        this.volatility = volatility;
        this.open = open;
    }

    @Override
    public void run() {
        // generate ticks
        double price = open;
        while(true) {
            price = Math.floor(price*100)/100;
            Tick tick = new Tick(this.symbol, price, cnt.incrementAndGet());
            tickReceivable.receiveTick(tick);
//            System.out.println("#"+cnt.get()+" "+this.symbol+" tick is queued");
            double waitTime = minWaitSec + Math.random()*(maxWaitSec-minWaitSec);
            price = getNextPrice(price, waitTime);
            try {
                int wait = (int)(waitTime*10);
                Thread.sleep(wait);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public double getNextPrice(double lastPrice, double deltaT) {
        double e = Math.random();
        double deltaS = lastPrice * (mu * (deltaT/7257600) + volatility * e * Math.sqrt(deltaT/7257600));
        return lastPrice + deltaS;
    }
}
