# README #
It is an application showcase a system giving an on screen display of the real-time value for a trader portfolio. It supports three types of products:
1. Common stocks.
2. European CAll options on common stocks.
3. European PUT options on common stocks.

## Program description:
### Main components
MarketDataManager.java
TraderPortfilio.java


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
#### Table: SECURITY_DEFINITION
* SYMBOL: (String), Primary Key, Symbol of the security
* ROOT_SYMBOL: (String), Root of symbol of the security. For the root of security, the ROOT_SYMBOL is its SYMBOL.
* TYPE: (String), Type of security. It supports 'STOCK','CALL','PUT'
* STRIKE: (Real), Strike price for stock price calculation
* MATURITY: (Real), Time to maturity (year) for stock price calculation
* RISK_FREE_INTEREST: (Real) Risk free interest rate for stock price calculation
* VOLATILITY: (Real) Volatility for stock price calculation
* MU: (Real), Mu, one of the parameter for Discrete Time Geometric Brownian motion 
* OPEN: (Real), The first price of the security when market open


1. It was implemented in SecuritiesUtils.loadSecurityStaticFromCVSAndDefinitionToList. It reads the given file and retrieve security definition from database. Save the definition and predefined caculator in universial cache. The portfolio contract detail is saved security static. This content is portfilio specific. It is stored in TraderPortfilio class. My thought is one import file as one TraderPortfilio object. Eventually, the application is able to handle multiple trader portfilio by creating number of object.    
2. As #1, it was implemented in SecuritiesUtils.java. The bean definition is in SecurityDefinition. The repository class is in SecurityDefintionRepository.
3. The mock market data provider is implemented in MarketDataProvider. It implements a runnable class. One MarketDataProvider holds one symbol. It ables to feed tick to TraderPortfolio which is registered in this class. It was desgined to handle mulitple TraderPortfilio objects.  
4. BlackScholesCache stores and calculate intermediate value when a root of symbol is retrieved. Everytime process tick, system can retrieve the intermediate value from its. The best we can do is to cache the latest tick and nav. When the new tick price does not change, we can reuse the old cache of Option price and NAV. 
5. TraderPortfolio is able realtime snapshot portfilio's market value and nav and send it to PortfilioPrinterListener.
6. PoerfilioPrinterListener listens to its blocking queue. When new element is added to queue, it takes the element and print it to console. 
