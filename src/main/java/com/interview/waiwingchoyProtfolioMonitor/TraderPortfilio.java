package com.interview.waiwingchoyProtfolioMonitor;

import com.interview.waiwingchoyProtfolioMonitor.bean.SecurityStatic;
import com.interview.waiwingchoyProtfolioMonitor.implement.TickProcessable;
import com.interview.waiwingchoyProtfolioMonitor.implement.TickReceivable;
import com.interview.waiwingchoyProtfolioMonitor.runnable.PortfilioPrinterListener;

import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.DoubleAdder;

public class TraderPortfilio implements TickProcessable, TickReceivable,Runnable {

    private final List<SecurityStatic> securityStaticList;
    private final ConcurrentHashMap<String, SecurityPrice> securityPriceMap = new ConcurrentHashMap<>();
    private final BlockingQueue<Tick> tickQueue = new LinkedBlockingQueue<>();
    private PortfilioPrinterListener portfilioPrinterListener = null;
    private final AtomicLong tickUpdateCount = new AtomicLong(0);
    private final UniversialCache universialCache = UniversialCache.getInstance();

    public TraderPortfilio(List<SecurityStatic> securityStaticList) {
        this.securityStaticList=securityStaticList;
        // init security variable content
        securityStaticList.forEach(securityStatic -> {
                securityPriceMap.put(securityStatic.symbol(), new SecurityPrice(securityStatic.symbol(),0,0));
        });
    }

    @Override
    public void processTick(Tick tick) {
        // process tick, realtime cal security price, value and NAV. These variable content are stored in securityPrice.
        BlackScholesCache cache = universialCache.getBlackScholesCache(tick.getSymbol());
        for (SecurityStatic securityStatic : securityStaticList) {
            if (universialCache.getSecurityDefinition(securityStatic.symbol()).getRootSymbol().equals(tick.getSymbol())) {
                double price = universialCache.getPriceCalculator(securityStatic.symbol()).calByCache(tick.getPrice(), cache);
                double value = price * securityStatic.positionSize();
                securityPriceMap.put(securityStatic.symbol(),new SecurityPrice(securityStatic.symbol(), price, value));
            }
        }
    }

    @Override
    public void receiveTick(Tick tick) {
        try {
            // enqueue tick to tick queue.
            tickQueue.put(tick);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void run() {
        init();
        while (true) {
            try {
                // dequeue tick from queue > process tick > send snapshot to printer
                Tick tick = tickQueue.take();
                processTick(tick);
                sendSnapshot(tick, tickUpdateCount.incrementAndGet());
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    // for market open
    public void init() {
        // handle the first tick for all securities base on the given screenshot.
        List<Tick> ticks = new CopyOnWriteArrayList<>();
        for (SecurityStatic securityStatic : securityStaticList) {
            if (universialCache.getSecurityDefinition(securityStatic.symbol()).getRootSymbol().equals(securityStatic.symbol())) {
                Tick tick = new Tick(securityStatic.symbol(), universialCache.getSecurityDefinition(securityStatic.symbol()).getOpen(), 0);
                processTick(tick);
                ticks.add(tick);
            }
        }
        sendOpenSnapshot(ticks,tickUpdateCount.incrementAndGet());
    }

    public void registerPortfilioPrinterRunner(PortfilioPrinterListener listener) {
        this.portfilioPrinterListener = listener;
    }

    public PortfilioSnapshot createSnapshot(Tick tick, long number) {
        DoubleAdder nav = new DoubleAdder();
        Map<String, SecurityPrice> priceMap = new ConcurrentHashMap<>();
        securityPriceMap.values().forEach(securityPrice -> {
            nav.add(securityPrice.getValue());
            priceMap.put(securityPrice.getSymbol(), new SecurityPrice(securityPrice.getSymbol(), securityPrice.getPrice(), securityPrice.getValue()));
        });
        return new PortfilioSnapshot(number, tick, priceMap, this.securityStaticList, nav.doubleValue());

    }

    public void sendSnapshot(Tick tick, long number) {
        if (portfilioPrinterListener!=null) {
            portfilioPrinterListener.receivePrintRequest(createSnapshot(tick, number));
        }
    }

    public PortfilioSnapshot createOpenSnapshot(List<Tick> ticks, long number) {

        Map<String, SecurityPrice> priceMap = new ConcurrentHashMap<>();
        DoubleAdder nav = new DoubleAdder();
        securityPriceMap.values().forEach(securityPrice -> {
                    nav.add(securityPrice.getValue());
                    priceMap.put(securityPrice.getSymbol(), new SecurityPrice(securityPrice.getSymbol(), securityPrice.getPrice(), securityPrice.getValue()));
                });
        return new PortfilioSnapshot(number, ticks, priceMap, this.securityStaticList, nav.doubleValue());
    }

    public void sendOpenSnapshot(List<Tick> ticks, long number) {
        if (portfilioPrinterListener!=null) {
            portfilioPrinterListener.receivePrintRequest(createOpenSnapshot(ticks, number));
        }
    }
}
