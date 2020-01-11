package com.accounted4.am.feature.finsec;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author glenn
 */
@RestController
@RequestMapping("/api/finsec")
public class FinSecController {

    @Autowired
    private InstrumentSolrDocRepository instrumentRepository;

    @RequestMapping(method = RequestMethod.GET, value = "/findBySymbol")
    public List<InstrumentSolrDoc> findBySymbol(@RequestParam final String searchTerm) {
        
        String leadWord = searchTerm.trim().split(" ")[0].toUpperCase();
        
        return instrumentRepository
                .findTop10BySymbolOrSymbolStartingWithOrDescrContainsOrderByScoreDescSymbolAsc(
                        leadWord,
                        leadWord,
                        searchTerm.split(" ")
                );
        
    }
    

}
