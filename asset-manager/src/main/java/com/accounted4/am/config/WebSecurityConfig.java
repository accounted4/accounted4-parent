package com.accounted4.am.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import com.accounted4.am.security.EnrichedJdbcUserDetailsManager;


@Configuration
@EnableWebSecurity  // Create the "springSecurityFilterChain" Servlet Filter
//@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {


    @Override
    protected void configure(final HttpSecurity http) throws Exception {
        http
            .authorizeRequests()
                .antMatchers("/", "/home").permitAll()
                .anyRequest().authenticated()
                .and()
            .formLogin()
                .loginPage("/login")
                .permitAll()
                .and()
            .logout()
//                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
//                .logoutSuccessUrl("/login?logout")
                .permitAll();
    }


    /**
     * The UserDetailsService for Spring Security to use. This is only used if the AuthenticationManagerBuilder
     *  has not been populated and no AuthenticationProviderBean is defined.
     *
     * @param datasource
     * @return
     */
    @Bean
    @Autowired
    public EnrichedJdbcUserDetailsManager userDetailsService(final DataSource datasource) {
        EnrichedJdbcUserDetailsManager enrichedJdbcUserDetailsManager = new EnrichedJdbcUserDetailsManager();
        enrichedJdbcUserDetailsManager.setDataSource(datasource);
        return enrichedJdbcUserDetailsManager;
    }


}
