package com.interview.waiwingchoyProtfolioMonitor;

import com.interview.waiwingchoyProtfolioMonitor.bean.SecurityDefinition;
import com.interview.waiwingchoyProtfolioMonitor.bean.SecurityStatic;

import java.util.List;
import java.util.Map;

public class PortfilioSnapshot implements PrintableObject{
//    private final List<Tick> ticks;
    private final Tick tick;
    private final List<Tick> ticks;
    private final Map<String, SecurityPrice> securityPriceMap;
    private final List<SecurityStatic> securityStaticList;
    private final long number;

    public PortfilioSnapshot(long number, List<Tick> ticks, Map<String, SecurityPrice> securityPriceMap, List<SecurityStatic> securityStaticList) {
        this.ticks=ticks;
        this.tick=null;
        this.securityPriceMap=securityPriceMap;
        this.securityStaticList=securityStaticList;
        this.number=number;
    }

    public PortfilioSnapshot(long number, Tick tick, Map<String, SecurityPrice> securityPriceMap, List<SecurityStatic> securityStaticList) {
        this.ticks=null;
        this.tick=tick;
        this.securityPriceMap=securityPriceMap;
        this.securityStaticList=securityStaticList;
        this.number=number;
    }

    @Override
    public String toConsole() {
        StringBuffer sb = new StringBuffer();

        sb.append("## ").append(number).append(" Market Data Update\n");
        if (ticks==null) {
            assert tick != null;
            sb.append(String.format("%s %s change to %.2f%n",tick.count,tick.getSymbol(),tick.getPrice()));
        }
        else {
            for (Tick thisTick: ticks) {
                sb.append(String.format("%s %s change to %.2f%n",thisTick.count, thisTick.getSymbol(),thisTick.getPrice()));
            }
        }
        sb.append("\n## Portfolio\n");
        sb.append(String.format("%-30s %20s %20s %20s%n", "symbol","price","qty","value"));
        double nav = 0d;
        for (SecurityStatic securityStatic : securityStaticList) {
            SecurityPrice price = securityPriceMap.get(securityStatic.securityDefinition().getSymbol());
            double value = price.getPrice()*securityStatic.positionSize();
            nav += value;
            sb.append(String.format("%-30s %,20.2f %,20.2f %,20.2f%n", securityStatic.securityDefinition().getSymbol(),price.getPrice(),Double.valueOf(securityStatic.positionSize()),value));
        }
        sb.append(String.format("%n#Total portfolio%,77.2f%n%n",nav));
        return sb.toString();
    }
}
