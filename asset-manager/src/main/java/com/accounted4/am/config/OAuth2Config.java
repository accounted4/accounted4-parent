package com.accounted4.am.config;

import com.accounted4.am.security.EnrichedJdbcUserDetailsManager;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.servlet.Filter;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.web.cors.CorsConfiguration;

/**
 * Configure the application to act as an oauth2 resource server (requiring a token for each request) and
 * also as an oauth2 authentication server for acquiring tokens.
 *
 * @author gheinze
 */
@Configuration
@EnableGlobalMethodSecurity(securedEnabled = true)
@EnableResourceServer
@EnableAuthorizationServer
public class OAuth2Config extends AuthorizationServerConfigurerAdapter {

    final AuthenticationManagerBuilder authenticationManager;

    @Autowired
    private DataSource dataSource;


    public OAuth2Config(AuthenticationManagerBuilder authenticationManager) {
        this.authenticationManager = authenticationManager;
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
        return enrichedJdbcUserDetailsManager;
    }


    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) {
        // Workaround for https://github.com/spring-projects/spring-boot/issues/1801
        endpoints.authenticationManager(authentication -> authenticationManager.getOrBuild().authenticate(authentication));
        endpoints.getFrameworkEndpointHandlerMapping().setCorsConfigurations(getCorsConfig());
    }


    private Map<String, CorsConfiguration> getCorsConfig() {

        Map<String, CorsConfiguration> corsConfigMap = new HashMap<>();
        corsConfigMap.put("/oauth/token", getOauthEndpointCorsConfig());
        //corsConfigMap.put("/api", getApiEndpointCorsConfig());

        return corsConfigMap;
    }


    private CorsConfiguration getOauthEndpointCorsConfig() {

        List<String> methods = Stream.of("GET", "POST", "PUT", "DELETE", "OPTIONS").collect(Collectors.toList());
        List<String> origins = Stream.of("*").collect(Collectors.toList());
        List<String> headers = Stream.of("Origin", "X-Requested-With", "Content-Type", "Accept", "Authorization").collect(Collectors.toList());

        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.setAllowedOrigins(origins);
        config.setAllowedMethods(methods);
        config.setAllowedHeaders(headers);

        return config;

    }


//    private CorsConfiguration getApiEndpointCorsConfig() {
//
//        List<String> methods = Stream.of("GET", "POST", "PUT", "DELETE", "OPTIONS").collect(Collectors.toList());
//        List<String> origins = Stream.of("*").collect(Collectors.toList());
//        List<String> headers = Stream.of("Authorization").collect(Collectors.toList());
//
//        CorsConfiguration config = new CorsConfiguration();
//        config.setAllowedOrigins(origins);
//        config.setAllowedMethods(methods);
//        config.setAllowedHeaders(headers);
//
//        return config;
//
//    }


    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.inMemory()
                .withClient("amClient")
                .secret("{noop}password")
                .authorizedGrantTypes("password", "authorization_code", "refresh_token")
                .scopes("read", "write")
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


    // Handle api OPTION pre-flight requests.
    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public Filter corsFilter() {
        return new SimpleCorsFilter();
    }



}
