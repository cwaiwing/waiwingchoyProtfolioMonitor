package com.interview.waiwingchoyProtfolioMonitor;

public class SecurityPrice {
    private final double price;
    private final double value;
    private final String symbol;
    public SecurityPrice(String symbol, double price, double value) {
        this.price = Math.floor(price*100)/100;
        this.symbol = symbol;
        this.value = value;
    }

    public double getPrice() {
        return price;
    }

    public String getSymbol() {
        return symbol;
    }

    public double getValue() {
        return value;
    }
}
