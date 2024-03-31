package com.interview.waiwingchoyProtfolioMonitor;
// Tick content storage
public class Tick{
    final String symbol;
    final double price;
    final long count;

    public Tick(String symbol, double price, long count) {
        this.symbol = symbol;
        this.price = price;
        this.count = count;
    }

    public String getSymbol() {
        return symbol;
    }

    public double getPrice() {
        return price;
    }

    @Override
    public Tick clone() {
        return new Tick(this);
    }

    protected Tick(Tick source) {
        this.symbol = source.symbol;
        this.price = source.price;
        this.count = source.count;
    }

    @Override
    public String toString(){
        return symbol+"|"+price;
    }
}
