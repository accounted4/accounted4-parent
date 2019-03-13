package com.accounted4.am.security;

import static com.accounted4.am.config.CustomTokenEnhancer.ADDITIONAL_INFO_USER_ACCOUNT_KEY;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Set;
import lombok.Data;
import org.springframework.security.oauth2.common.OAuth2AccessToken;

/**
 * DTO with Oauth token info.
 *
 * @author glenn
 */
@Data
public class OauthToken {

    private String accessToken;
    private Set<String> scope;
    private String tokenType;
    private boolean expired;
    private String expiration;    
    private String refreshToken;
    private String userAccount;

    
    OauthToken(final OAuth2AccessToken token) {
        
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        accessToken = token.getValue();
        scope = token.getScope();
        tokenType = token.getTokenType();
        expired = token.isExpired();
        expiration = df.format(token.getExpiration());
        refreshToken = token.getRefreshToken().getValue();
        userAccount = (String)token
                .getAdditionalInformation()
                .getOrDefault(ADDITIONAL_INFO_USER_ACCOUNT_KEY, "unknown")
                ;
    }
    
}
