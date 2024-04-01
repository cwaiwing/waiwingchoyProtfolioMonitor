package com.interview.waiwingchoyProtfolioMonitor;

import com.interview.waiwingchoyProtfolioMonitor.bean.SecurityDefinition;

public class BlackScholesCache {
    //strike
    private final double X;
    //risk free interest
    private final double r;
    //maturity
    private final double T;
    //volatility
    private final double sigma;
    protected final double kert;
    private final double sigmaXsqrtT;
    private final double sigmaSquare;
    private final double sqrtT;
    protected volatile double d1Part2;


    public BlackScholesCache(SecurityDefinition s) {
        this(s.getStrike(), s.getRiskFreeInterest(), s.getMaturity(), s.getVolatility());
    }
    public BlackScholesCache(double X, double r, double T, double sigma) {
        this.X = X;
        this.r = r;
        this.T = T;
        this.sigma = sigma;
        this.sqrtT=Math.sqrt(T);
        this.kert = X * Math.exp(-r * T);
        this.sigmaXsqrtT = sigma * sqrtT;
        this.sigmaSquare = sigma * sigma;
        this.d1Part2=((r + (sigmaSquare)/2) * sqrtT) / sigma;
    }

    public double getX() {
        return X;
    }

    public double getKert() {
        return kert;
    }

    public double getSigmaXsqrtT() {
        return sigmaXsqrtT;
    }

    public double getD1Part2() {
        return d1Part2;
    }
}
