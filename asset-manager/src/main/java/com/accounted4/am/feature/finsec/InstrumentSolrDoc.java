package com.accounted4.am.feature.finsec;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.solr.client.solrj.beans.Field;
import org.springframework.data.annotation.Id;
import org.springframework.data.solr.core.mapping.SolrDocument;
import org.springframework.data.solr.repository.Score;


/**
 *
 * A Solr document representing a financial securities instrument (stock, bond,
 * ...)
 *
 * @author glenn
 */
@Data
@EqualsAndHashCode
@SolrDocument(solrCoreName = "financial_instruments")
public class InstrumentSolrDoc implements Comparable<InstrumentSolrDoc>{

    @Id
    @Field
    private String id;

    @Field
    private String symbol;

    @Field
    private String descr;

    @Score
    private Float score;
    
    @Override
    public int compareTo(InstrumentSolrDoc t) {
        int scoreCompare = Float.compare(score, t.getScore());
        return (0 == scoreCompare) ? symbol.compareTo(t.getSymbol()) : scoreCompare;
    }
    
}
