package com.accounted4.am.feature.finsec;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
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
    private InstrumentSolrDocRepository instrumentSolrDocRepo;

    @Autowired
    private DebentureRepository debentureRepo;
    
    @Autowired
    private QuoteRepository quoteRepository;
    
    
    
    @GetMapping(value = "/findBySymbol")
    public List<InstrumentSolrDoc> findBySymbol(@RequestParam final String searchTerm) {
        
        String leadWord = searchTerm.trim().split(" ")[0].toUpperCase();
        
        return instrumentSolrDocRepo
                .findTop10BySymbolOrSymbolStartingWithOrDescrContainsOrderByScoreDescSymbolAsc(
                        leadWord,
                        leadWord,
                        searchTerm.split(" ")
                );
        
    }
    
    
    
    @GetMapping(value = "/debentures")
    public Page<DebentureEntity> getDebentures(Pageable pageRequest) {
        return debentureRepo.findAll(pageRequest);
    }


    @GetMapping(value = "/quotes/{instrumentId}")
    public List<QuoteEntity> getQuotes(@PathVariable Integer instrumentId) {
        return quoteRepository.getQuotes(instrumentId);
    }


}
