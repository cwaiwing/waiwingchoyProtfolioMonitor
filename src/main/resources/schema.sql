DROP TABLE SECURITY_DEFINITION;
create table if not exists SECURITY_DEFINITION ('SYMBOL' varchar(20) not null, 'ROOT_SYMBOL' varchar(20) not null, 'TYPE' varchar(10), 'STRIKE' REAL, 'MATURITY' REAL, 'RISK_FREE_INTEREST' REAL, 'VOLATILITY' REAL, 'MU' REAL, 'OPEN' REAL, PRIMARY KEY('SYMBOL'));