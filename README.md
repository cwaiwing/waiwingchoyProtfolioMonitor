# README #
It is an application showcase a system to show the real-time value for a trader portfolio. It supports three types of products:
1. Common stocks.
2. European CAll options on common stocks.
3. European PUT options on common stocks.

## Requirement:
Gradle

## Build:
````
./gradlew build
````

## Execute:
````
java -jar build/libs/waiwingchoyProtfolioMonitor-0.0.1-SNAPSHOT.jar [portfolio file path]
ie. java -jar build/libs/waiwingchoyProtfolioMonitor-0.0.1-SNAPSHOT.jar portfolio.txt
````

## Trader Portfilio file example
````
symbol,positionSize
AAPL,200
AAPL-OCT-2020-110-C,-20000
AAPL-OCT-2020-110-P,20000
TESLA,500
TESLA-OCT-2020-450-C,-10000
TESLA-OCT-2020-450-P,10000
````

## Securities definition file
````
src/main/resources/data.sql
````

The content should look like this:
````
INSERT INTO SECURITY_DEFINITION VALUES ('AAPL','AAPL','STOCK',110.0,0.0833,0.02,0.2,0.2,110);
INSERT INTO SECURITY_DEFINITION VALUES ('AAPL-OCT-2020-110-C','AAPL','CALL',110.0,0.0833,0.02,0.2,0.2,0);
INSERT INTO SECURITY_DEFINITION VALUES ('AAPL-OCT-2020-110-P','AAPL','PUT',110.0,0.0833,0.02,0.2,0.2,0);
````

## Securities definition database schema
`````
Table: SECURITY_DEFINITION
SYMBOL: (String), Primary Key, Symbol of the security
ROOT_SYMBOL: (String), Root of symbol of the security. For the root of security, the ROOT_SYMBOL is its SYMBOL.
TYPE: (String), Type of security. It supports 'STOCK','CALL','PUT'
STRIKE: (Real), Strike price for stock price calculation
MATURITY: (Real), Time to maturity (year) for stock price calculation
RISK_FREE_INTEREST: (Real) Risk free interest rate for stock price calculation
VOLATILITY: (Real) Volatility for stock price calculation
MU: (Real), Mu, one of the parameter for Discrete Time Geometric Brownian motion 
OPEN: (Real), The first price of the security when market open
`````
