package com.interview.waiwingchoyProtfolioMonitor;

public class SecurityPrice {
    private final double price;
    private final String symbol;
    public SecurityPrice(String symbol, double price) {
        this.price = Math.floor(price*100)/100;
        this.symbol = symbol;
    }

    public double getPrice() {
        return price;
    }

    public String getSymbol() {
        return symbol;
    }
}
