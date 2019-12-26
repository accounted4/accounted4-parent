package com.accounted4.am.config;

import com.accounted4.am.security.EnrichedJdbcUserDetailsManager;
import java.util.Arrays;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;
//import org.springframework.web.cors.CorsConfiguration;
//import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
//import org.springframework.web.filter.CorsFilter;

/**
 * Configure the application to act as an oauth2 authentication server for acquiring tokens.
 *
 * @author gheinze
 */
@Configuration
@EnableGlobalMethodSecurity(securedEnabled = true)
@EnableAuthorizationServer
public class OAuthAuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {

    
    private final AuthenticationManagerBuilder authenticationManager;
    private final AssetManagerProperties assetManagerProperties;
    private final DataSource dataSource;
    

    @Autowired
    public OAuthAuthorizationServerConfig(
            final AuthenticationManagerBuilder authenticationManager,
            final AssetManagerProperties assetManagerProperties,
            final DataSource dataSource
            
    ) {
        this.authenticationManager = authenticationManager;
        this.assetManagerProperties = assetManagerProperties;
        this.dataSource = dataSource;
    }



    // Still had to enable SimpleCorsFilter to let OPTIONS flow through
    // Below is how it -should- be configured, but still get stuck by CORS.
//    @Bean
//    public CorsFilter corsFilter() {
//
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        CorsConfiguration config = new CorsConfiguration();
//        config.setAllowCredentials(true); // you USUALLY want this
//        config.addAllowedOrigin("*");
//        config.addAllowedHeader("*");
//        config.addAllowedMethod("OPTIONS");
//        config.addAllowedMethod("HEAD");
//        config.addAllowedMethod("GET");
//        config.addAllowedMethod("PUT");
//        config.addAllowedMethod("POST");
//        config.addAllowedMethod("DELETE");
//        config.addAllowedMethod("PATCH");
//        source.registerCorsConfiguration("/**", config);
//        return new CorsFilter(source);
//    }


    @Bean
    public TokenStore tokenStore() {
        // InMemoryTokenStore, JwtTokenStore, custom, ...
        return new JdbcTokenStore(dataSource);
    }

    
    @Bean
    public TokenEnhancer tokenEnhancer() {
        return new CustomTokenEnhancer();
    }

    
    @Bean
    @Primary
    public DefaultTokenServices tokenServices() {
        DefaultTokenServices defaultTokenServices = new DefaultTokenServices();
        defaultTokenServices.setTokenStore(tokenStore());
        defaultTokenServices.setSupportRefreshToken(true);
        defaultTokenServices.setTokenEnhancer(tokenEnhancer());
        return defaultTokenServices;
    }

    
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    @Autowired
    public EnrichedJdbcUserDetailsManager userDetailsService(final DataSource datasource) {
        EnrichedJdbcUserDetailsManager enrichedJdbcUserDetailsManager = new EnrichedJdbcUserDetailsManager();
        enrichedJdbcUserDetailsManager.setDataSource(datasource);
        //createBootstrapUser(enrichedJdbcUserDetailsManager);
        return enrichedJdbcUserDetailsManager;
    }
    
//    private void createBootstrapUser(EnrichedJdbcUserDetailsManager jdbcUserDetailsManager) {
//        jdbcUserDetailsManager.createUser(
//                User
//                        .withDefaultPasswordEncoder()
//                        .username("superuser@accounted4.com")
//                        .password("superuserpwd")
//                        .roles("SUPERUSER")
//                        .build()
//        );        
//    }



    
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) {
        
        // Wire in a TokenStore, otherwise Spring Security will create it's own default one.
        endpoints.tokenStore(tokenStore());
        
        // Workaround for https://github.com/spring-projects/spring-boot/issues/1801
        // This is to make sure the authenticationManager is created earlier in the sequence
        // before another component initiates a default one.
        
        // Without this work around, requesting a token will result in 
        // {
        //    "error": "unsupported_grant_type",
        //    "error_description": "Unsupported grant type: password"
        // }        
        endpoints.authenticationManager(authentication -> authenticationManager.getOrBuild().authenticate(authentication));
        
        // Required to fulfill oauth2 refresh token requests
        endpoints.userDetailsService(userDetailsService(dataSource));

        
        // Enrich the information returned with the oauth token
        TokenEnhancerChain enhancerChain = new TokenEnhancerChain();
        enhancerChain.setTokenEnhancers(Arrays.asList(tokenEnhancer()));
    
        endpoints.tokenEnhancer(enhancerChain);
    }

    
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.inMemory()
                .withClient("amClient")
                .secret("{noop}password")
                .authorizedGrantTypes("password", "authorization_code", "refresh_token")
                .scopes("read", "write")
                .accessTokenValiditySeconds(assetManagerProperties.getOauthTokenLifespanSeconds())
                ;
    }


    /*
     * Web browsers send the pre-flight "OPTIONS" message which expects the "Access-Control-Allow-Methods"
     * response. We don't want to block with a 401 Unauthorized, so put a filter early in the chain to
     * accept OPTIONS.
    */
    @Override
    public void configure(AuthorizationServerSecurityConfigurer oauthServer) {
        // Disable /oauth/token Http Basic Auth
        oauthServer.allowFormAuthenticationForClients();
    }


}

