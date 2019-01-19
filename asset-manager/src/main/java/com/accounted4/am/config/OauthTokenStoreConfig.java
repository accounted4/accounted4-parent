package com.accounted4.am.config;

import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;

/**
 * Setup a bean to expose the Oauth TokenStore. The store can be used 
 * for services such as token revocation.
 * 
 * @author glenn
 */
@Configuration
public class OauthTokenStoreConfig {

    private final DataSource dataSource;

    @Autowired
    public OauthTokenStoreConfig(DataSource dataSource) {
        this.dataSource = dataSource;
    }
    
    @Bean
    public TokenStore tokenStore() {
        // InMemoryTokenStore, JwtTokenStore, custom, ...
        return new JdbcTokenStore(dataSource);
    }

    @Bean
    @Primary
    public DefaultTokenServices tokenServices() {
        DefaultTokenServices defaultTokenServices = new DefaultTokenServices();
        defaultTokenServices.setTokenStore(tokenStore());
        defaultTokenServices.setSupportRefreshToken(true);
        return defaultTokenServices;
    }

}
