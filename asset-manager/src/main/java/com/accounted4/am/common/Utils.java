package com.accounted4.am.common;

import com.accounted4.am.security.EnrichedUserDetails;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 *
 * @author gheinze
 */
public class Utils {

    public static EnrichedUserDetails getUserDetails() {
       EnrichedUserDetails principal = (EnrichedUserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
       return principal;
    }

}
