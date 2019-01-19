package com.accounted4.am.common;

import com.accounted4.am.security.EnrichedUserDetails;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Common shortcut snippets.
 *
 * @author gheinze
 */
public class Utils {

    // Prevent instantiation of utility class
    private Utils() {
    }
    
    
    /**
     * Retrieve the Principal from the Spring Security Context and cast it to
     * the enriched user used by the application.
     * 
     * @return An EnrichedUserDetails spring security Principal
     */
    public static EnrichedUserDetails getUserDetails() {
        
       EnrichedUserDetails principal = 
               (EnrichedUserDetails) SecurityContextHolder.getContext()
               .getAuthentication()
               .getPrincipal()
               ;
       
       return principal;
    }

}
