package com.accounted4.am.config;

import com.accounted4.am.security.EnrichedUserDetails;
import java.util.HashMap;
import java.util.Map;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;


/**
 * Add additional information to an oauth token object.
 * 
 * @author glenn
 */
public class CustomTokenEnhancer implements TokenEnhancer {

    public static String ADDITIONAL_INFO_USER_ACCOUNT_KEY = "userAccount";
    
    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {

        EnrichedUserDetails user = (EnrichedUserDetails) authentication.getPrincipal();
        final Map<String, Object> additionalInfo = new HashMap<>();

        additionalInfo.put(ADDITIONAL_INFO_USER_ACCOUNT_KEY, user.getUsername());

        ((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(additionalInfo);
        
        return accessToken;
    }
    

}
