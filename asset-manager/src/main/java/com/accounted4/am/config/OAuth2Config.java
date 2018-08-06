package com.accounted4.am.config;

import com.accounted4.am.security.EnrichedJdbcUserDetailsManager;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;

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
    }


    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.inMemory()
                .withClient("amClient")
                .secret("{noop}password")
                .authorizedGrantTypes("password", "authorization_code", "refresh_token")
                .scopes("read", "write")
                ;
    }


    
    //curl -X POST -vu amClient:password http://localhost:8080/oauth/token -H "Accept: application/json" -d "password=r00table&username=superuser@accounted4.com&grant_type=password&scope=write"
    //
    //{"access_token":"a4bc6b84-4420-42cc-945c-0bfec34a7147","token_type":"bearer","refresh_token":"7fa0962b-46c5-4135-b947-6227d47cc999","expires_in":43199,"scope":"write"}
    //
    //curl -i -H "Accept: application/json" -H "Authorization: Bearer a4bc6b84-4420-42cc-945c-0bfec34a7147" -X GET http://localhost:8080/useradmin


}
