package com.accounted4.am.feature.finsec;

import java.util.List;
import org.springframework.data.solr.repository.Boost;
import org.springframework.data.solr.repository.SolrCrudRepository;


/**
 * 
 * @author glenn
 */
public interface InstrumentSolrDocRepository  extends SolrCrudRepository<InstrumentSolrDoc, String> {

    List<InstrumentSolrDoc> findTop10BySymbolOrSymbolStartingWithOrDescrContainsOrderByScoreDescSymbolAsc(
            @Boost(3)String symbolExact,
            @Boost(2)String symbolStart,
            String[] descrContains
    );
    
}
