package com.accounted4.am.security;

import com.accounted4.am.common.RestResponse;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.token.ConsumerTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Oauth Token management.
 * 
 * @author glenn
 */
@RestController
@RequestMapping("/api/oauthToken")
public class OauthTokenManagementController {

    
    @Resource(name = "tokenStore")
    TokenStore tokenStore;

    @Resource(name = "tokenServices")
    ConsumerTokenServices tokenServices;

    
    @RequestMapping(method = RequestMethod.GET, value = "/tokens/{clientId}")
    @ResponseBody
    public RestResponse<List<OauthToken>> getTokens(@PathVariable final String clientId) {

        Collection<OAuth2AccessToken> tokens = tokenStore.findTokensByClientId(clientId);
        
        List<OauthToken> dtoTokens = tokens.stream()
                .map(t -> new OauthToken(t))
                .collect(Collectors.toList())
                ;
        
        return new RestResponse(dtoTokens);
    }
    

    @RequestMapping(method = RequestMethod.POST, value = "/tokens/revoke/{tokenId:.+}")
    @ResponseBody
    public void revokeToken(@PathVariable final String tokenId) {
        tokenServices.revokeToken(tokenId);
    }

}
