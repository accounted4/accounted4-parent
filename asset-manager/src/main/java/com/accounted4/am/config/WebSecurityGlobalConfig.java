package com.accounted4.am.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 *
 * @author glenn
 */
@Configuration
public class WebSecurityGlobalConfig extends WebSecurityConfigurerAdapter {

    /**
     * Return without any further filter chain processing on the receipt of an OPTIONS
     * pre-flight request.  This is necessary to avoid having to add CORS filters
     * in the case of oauth token retrieval.
     * 
     * See:  https://stackoverflow.com/questions/30632200/standalone-spring-oauth2-jwt-authorization-server-cors/30638914#30638914
     * 
     * @param web
     * @throws Exception 
     */
    @Override
      public void configure(WebSecurity web) throws Exception {
        web.ignoring()
          .antMatchers(HttpMethod.OPTIONS);
      }   
      
}
