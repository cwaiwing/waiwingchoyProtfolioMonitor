package com.interview.waiwingchoyProtfolioMonitor;

import com.interview.waiwingchoyProtfolioMonitor.bean.SecurityStatic;

import java.util.List;
import java.util.Map;

// Portfili snapshort storage
public class PortfilioSnapshot implements PrintableObject{
//    private final List<Tick> ticks;
    private final Tick tick;
    private final List<Tick> ticks;
    private final Map<String, SecurityPrice> securityPriceMap;
    private final List<SecurityStatic> securityStaticList;
    private final long number;
    private final double nav;

    public PortfilioSnapshot(long number, List<Tick> ticks, Map<String, SecurityPrice> securityPriceMap, List<SecurityStatic> securityStaticList, double nav) {
        this.ticks=ticks;
        this.tick=null;
        this.securityPriceMap=securityPriceMap;
        this.securityStaticList=securityStaticList;
        this.number=number;
        this.nav=nav;
    }

    public PortfilioSnapshot(long number, Tick tick, Map<String, SecurityPrice> securityPriceMap, List<SecurityStatic> securityStaticList, double nav) {
        this.ticks=null;
        this.tick=tick;
        this.securityPriceMap=securityPriceMap;
        this.securityStaticList=securityStaticList;
        this.number=number;
        this.nav=nav;
    }

    @Override
    public String toConsole() {
        StringBuffer sb = new StringBuffer();

        sb.append("## ").append(number).append(" Market Data Update\n");
        if (ticks==null) {
            assert tick != null;
            sb.append(String.format("%s change to %.2f%n",tick.getSymbol(),tick.getPrice()));
        }
        else {
            for (Tick thisTick: ticks) {
                sb.append(String.format("%s %s change to %.2f%n",thisTick.count, thisTick.getSymbol(),thisTick.getPrice()));
            }
        }
        sb.append("\n## Portfolio\n");
        sb.append(String.format("%-30s %20s %20s %20s%n", "symbol","price","qty","value"));

        for (SecurityStatic securityStatic : securityStaticList) {
            SecurityPrice price = securityPriceMap.get(securityStatic.securityDefinition().getSymbol());
            sb.append(String.format("%-30s %,20.2f %,20.2f %,20.2f%n", securityStatic.securityDefinition().getSymbol(),price.getPrice(), (double) securityStatic.positionSize(),price.getValue()));
        }
        sb.append(String.format("%n#Total portfolio%,77.2f%n%n",this.nav));
        return sb.toString();
    }
}
