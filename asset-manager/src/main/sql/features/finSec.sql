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
   id      serial PRIMARY KEY
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
   id               serial PRIMARY KEY
  ,exchange_id      integer
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
  ,instrument_id  integer
  ,read_dte       date
  ,close_price    money
  ,volume_traded  integer
  ,CONSTRAINT sm_eod_quote_instrument_id_fk FOREIGN KEY(instrument_id) REFERENCES sm_instrument(id)
)
;

CREATE UNIQUE INDEX sm_eod_quote_instrument_id_read_dte_fk ON sm_eod_quote(instrument_id, read_dte)
;


CREATE TABLE sm_debenture_detail(
   instrument_id     integer PRIMARY KEY
  ,percentage        decimal
  ,issue_dte         date
  ,maturity_dte      date
  ,prospectus        TEXT
  ,underlying_instrument_id integer
  ,conversion_price numeric(15, 2)
  ,comments         TEXT

  ,CONSTRAINT sm_debenture_detail_instrument_id_fk
     FOREIGN KEY(instrument_id)
     REFERENCES sm_instrument(id)
    
  ,CONSTRAINT sm_debenture_detail_underlying_id_fk
     FOREIGN KEY(underlying_instrument_id)
     REFERENCES sm_instrument(id)
    
)
;


CREATE OR REPLACE VIEW sm_quote_vw 
AS
SELECT i.id AS instrument_id, i.symbol, i.descr, i.instrument_type, i.instrument_status, q.close_price, q.volume_traded, q.read_dte
  FROM sm_instrument i
  LEFT JOIN sm_eod_quote q
         ON (q.instrument_id = i. id
			 AND
			 q.read_dte = (SELECT MAX(read_dte) FROM sm_eod_quote WHERE instrument_id = q.instrument_id)
			)
  ORDER BY i.descr
;


CREATE OR REPLACE VIEW sm_debenture_vw 
AS
SELECT q.instrument_id, q.symbol, q.descr, d.issue_dte, d.maturity_dte, d.percentage
       ,ROUND(d.percentage - ((q.close_price::decimal - 100.0) / ((d.maturity_dte - current_date) / 365.0)), 3) AS effective_rate
       ,q.close_price, q.volume_traded, q.read_dte
       ,d.underlying_instrument_id, uq.symbol AS underlying_symbol
       ,uq.close_price AS underlying_close_price
       ,uq.read_dte AS underlying_read_dte
       ,d.conversion_price
       ,ROUND(100/d.conversion_price::decimal, 3) AS conversion_rate
       ,ROUND(uq.close_price::decimal * 100/d.conversion_price::decimal, 3) AS converted
       ,d.prospectus
       ,d.comments
  FROM sm_quote_vw q
  LEFT JOIN sm_debenture_detail d ON (d.instrument_id = q.instrument_id)
  LEFT JOIN sm_quote_vw uq ON (uq.instrument_id = d.underlying_instrument_id)
  WHERE q.instrument_type = 'D'
    AND q.instrument_status = 'A'
  ORDER BY q.symbol
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
    INSERT INTO sm_instrument(exchange_id, symbol, descr, create_dte, instrument_status, instrument_type)
      SELECT v_exchange_id, symbol, security_descr, v_dte, 'A'
            ,CASE 
               WHEN symbol LIKE '%.DB%' THEN 'D'
               WHEN symbol LIKE '%.PR%' THEN 'P'
             END
        FROM sm_eod_quotes_staging
        WHERE (v_exchange_id, symbol) NOT IN (SELECT exchange_id, symbol FROM sm_instrument)
        ORDER BY symbol
    ;

    -- Copy end of day quotes
    INSERT INTO sm_eod_quote(instrument_id, read_dte, close_price, volume_traded)
      SELECT s.id, v_dte, e.close_price::numeric, to_number(e.volume_traded, '999G999G999')
        FROM sm_eod_quotes_staging e
        JOIN sm_instrument s ON (s.exchange_id = v_exchange_id AND s.symbol = e.symbol)
        ORDER BY e.symbol
    ;
    
    -- Mark missing securities as inactive
    UPDATE sm_instrument
      SET instrument_status = 'I'
      WHERE exchange_id = v_exchange_id
        AND symbol IN (
              SELECT symbol FROM sm_instrument WHERE exchange_id = v_exchange_id
              EXCEPT
              SELECT symbol FROM sm_eod_quotes_staging
            )
    ;    

END;
$$ LANGUAGE plpgsql;

