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
import java.util.stream.Collectors;

public class TraderPortfilio implements TickProcessable, TickReceivable,Runnable {

    private final List<SecurityStatic> securityStaticList;
    private final ConcurrentHashMap<String, SecurityPrice> securityPriceMap = new ConcurrentHashMap<>();
    private final BlockingQueue<Tick> tickQueue = new LinkedBlockingQueue<>();
    private PortfilioPrinterListener portfilioPrinterListener = null;
    private final AtomicLong tickUpdateCount = new AtomicLong(0);
    private List<Tick> initTicks=null;

    public TraderPortfilio(List<SecurityStatic> securityStaticList) {
        this.securityStaticList=securityStaticList;
        securityStaticList.forEach(securityStatic ->
                securityPriceMap.put(securityStatic.securityDefinition().getSymbol(), new SecurityPrice(securityStatic.securityDefinition().getSymbol(),0)));
    }

    @Override
    public void processTick(Tick tick) {
        // update price
        securityStaticList.stream().filter(c -> c.securityDefinition().getRootSymbol().equals(tick.getSymbol())).forEach(securityStatic ->
                securityPriceMap.put(securityStatic.securityDefinition().getSymbol(), new SecurityPrice(securityStatic.securityDefinition().getSymbol(), securityStatic.priceCalculator().calculatePrice(tick.getPrice(), securityStatic.securityDefinition())))
        );
    }

    @Override
    public void receiveTick(Tick tick) {
        try {
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
                Tick tick = tickQueue.take();
                processTick(tick);
                sendOpenSnapshot(tick, tickUpdateCount.incrementAndGet());
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    // for market open
    public void init() {
        List<Tick> ticks = new CopyOnWriteArrayList<>();
        for (SecurityStatic securityStatic : securityStaticList) {
            if (securityStatic.securityDefinition().getRootSymbol().equals(securityStatic.securityDefinition().getSymbol())) {
                Tick tick = new Tick(securityStatic.securityDefinition().getSymbol(), securityStatic.securityDefinition().getOpen(), 0);
                processTick(tick);
                ticks.add(tick);
            }
        }
        sendOpenSnapshot(ticks,tickUpdateCount.incrementAndGet());
    }

    public void registerPortfilioPrinterRunner(PortfilioPrinterListener listener) {
        this.portfilioPrinterListener = listener;
    }

    public PortfilioSnapshot createOpenSnapshot(Tick tick, long number) {

        Map<String, SecurityPrice> priceMap = securityPriceMap.values().stream().collect(Collectors.toMap(SecurityPrice::getSymbol, securityPrice -> new SecurityPrice(securityPrice.getSymbol(), securityPrice.getPrice())));
        return new PortfilioSnapshot(number, tick, priceMap, this.securityStaticList);

    }

    public void sendOpenSnapshot(Tick tick, long number) {
        if (portfilioPrinterListener!=null) {
            portfilioPrinterListener.receivePrintRequest(createOpenSnapshot(tick, number));
        }
    }

    public PortfilioSnapshot createOpenSnapshot(List<Tick> ticks, long number) {

        Map<String, SecurityPrice> priceMap = new ConcurrentHashMap<>();
        securityPriceMap.values().forEach(securityPrice -> priceMap.put(securityPrice.getSymbol(), new SecurityPrice(securityPrice.getSymbol(), securityPrice.getPrice())));
        return new PortfilioSnapshot(number, ticks, priceMap, this.securityStaticList);

    }

    public void sendOpenSnapshot(List<Tick> ticks, long number) {
        if (portfilioPrinterListener!=null) {
            portfilioPrinterListener.receivePrintRequest(createOpenSnapshot(ticks, number));
        }
    }
    public void setInitTicks(List<Tick> ticks) {
        this.initTicks=ticks;
    }
}
