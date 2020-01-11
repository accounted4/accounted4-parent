SET search_path=public;

SET ROLE assetmanager;

------------------------------
-- Financial Securities Module
------------------------------

/*

DROP TABLE sm_eod_quote;
DROP TABLE sm_instrument;
DROP TABLE sm_exchange;
DROP TABLE sm_eod_quotes_staging;
DROP FUNCTION sm_load_eod_quotes;

CREATE UNLOGGED TABLE sm_eod_quotes_staging (
    symbol              TEXT,
    security_descr      TEXT,
    close_price         DECIMAL,
    volume_traded       TEXT
)
;

*/        
        
CREATE TABLE sm_exchange(
   id      bigserial PRIMARY KEY
  ,symbol  TEXT
  ,descr   TEXT
)
;


INSERT INTO sm_exchange(symbol, descr)
 VALUES ('TSX', 'Toronto Stock Exchange')
       ,('TSXV', 'Toronto Venture Exchange')
       ,('NYSE', 'New York Stock Exchange')
       ,('NASDAQ', 'NASDAQ Stock Exchange')
;


CREATE TABLE sm_instrument(
   id               bigserial PRIMARY KEY
  ,exchange_id      bigserial
  ,symbol           TEXT
  ,instrument_type  TEXT
  ,descr            TEXT
  ,create_dte       DATE
  ,instrument_status TEXT
  ,CONSTRAINT sm_instrument_exchange_id_fk FOREIGN KEY(exchange_id) REFERENCES sm_exchange(id)
)
;

CREATE UNIQUE INDEX sm_instrument_exchange_symbol_fk ON sm_instrument(exchange_id, symbol)
;

CREATE TABLE sm_eod_quote(
   id             bigserial PRIMARY KEY
  ,instrument_id  bigserial
  ,read_dte       date
  ,close_price    money
  ,volume_traded  integer
  ,CONSTRAINT sm_eod_quote_instrument_id_fk FOREIGN KEY(instrument_id) REFERENCES sm_instrument(id)
)
;

CREATE UNIQUE INDEX sm_eod_quote_instrument_id_read_dte_fk ON sm_eod_quote(instrument_id, read_dte)
;

CREATE OR REPLACE FUNCTION sm_load_eod_quotes(p_exchange TEXT, p_dte TEXT)
RETURNS VOID AS $$
DECLARE

  v_exchange_id  sm_exchange.id%TYPE;
  v_dte  date;
  
BEGIN

    SELECT id INTO v_exchange_id FROM sm_exchange WHERE symbol = p_exchange;
    SELECT to_date(substring(p_dte, 1, 10), 'YYYY-MM-DD') INTO v_dte;

    -- Define newly encountered securities        
    INSERT INTO sm_instrument(exchange_id, symbol, descr, create_dte)
      SELECT v_exchange_id, symbol, security_descr, v_dte
        FROM sm_eod_quotes_staging
        WHERE (v_exchange_id, symbol) NOT IN (SELECT exchange_id, symbol FROM sm_instrument)
        ORDER BY symbol
    ;

    -- Copy end of day quotes
    INSERT INTO sm_eod_quote(instrument_id, read_dte, close_price, volume_traded)
      SELECT s.id, v_dte, e.close_price::money, to_number(e.volume_traded, '999G999G999')
        FROM sm_eod_quotes_staging e
        JOIN sm_instrument s ON (s.exchange_id = v_exchange_id AND s.symbol = e.symbol)
        ORDER BY e.symbol
    ;
    
END;
$$ LANGUAGE plpgsql;

