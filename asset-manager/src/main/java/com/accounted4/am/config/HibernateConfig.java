package com.accounted4.am.config;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import org.hibernate.MultiTenancyStrategy;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.accounted4.am.AssetManagerApplication;
import com.accounted4.am.security.EnrichedUserDetails;
import com.accounted4.commons.db.PostgresConnectConfig;
import com.accounted4.commons.db.PostgresSchemaMultiTenantConnectionProvider;
import com.accounted4.commons.db.UserSessionCurrentTenantIdentifierResolver;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.util.function.Supplier;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;


/**
 * Override Spring Boots default usage of spring.jpa.* properties from the application.properties
 * file in order to wire in multi-tenancy support. Once this is used, it seems no spring.jpa properties
 * will be picked up.
 *
 * Currently tests run using embedded hsql, so we do not wish to use this configuration for testing.
 * The embedded db allows the tests to run without any database dependencies. Most services should
 * still be testable this way.  When there is a divergence for Postgresql or a need to test multi-
 * tenancy, then the environment variable DATABASE_URL will need to be configured to point to a test
 * Postgresql database service and the @Profile("!test") annotation can be removed.
 *
 * @author gheinze
 */
@Slf4j
@Profile("!test")
@Configuration
public class HibernateConfig {


    // SpringBoot automatically adds the AnonymousAuthenticationFilter and an AnonymousAuthenticationProvider.
    // This means unauthenticated users will automatically have the granted-authority of ROLE_ANONYMOUS
    // and a username of "anonymousUser".
    // This also means there will never be a "null" Principal retrieved from the security context.
    private static final String ANONYMOUS_USER = "anonymousUser";

    private static final String GLOBAL_SCHEMA_KEY = "db.config.tenant.global";


    private final Environment env;

    @Autowired
    public HibernateConfig(final Environment env) {
        this.env = env;
    }


    /**
     * Create a pooled dataSource based on the db connect information extracted from the
     * system environment variable DATABASE_URL. Format:
     *
     *      postgres://&lt;username&gt;:&lt;password&gt;@&lt;host&gt;/&lt;dbname&gt;
     *
     * @return
     * @throws java.net.URISyntaxException
     */
    @Bean
    public HikariDataSource dataSource() throws URISyntaxException {

        PostgresConnectConfig pgConnectConfig = new PostgresConnectConfig();

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(pgConnectConfig.getDbUrl());
        config.setUsername(pgConnectConfig.getUsername());
        config.setPassword(pgConnectConfig.getPassword());
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

        HikariDataSource ds = new HikariDataSource(config);

        return ds;
    }


    @Bean
    public JpaVendorAdapter jpaVendorAdapter() {
        return new HibernateJpaVendorAdapter();
    }


    @Bean
    public MultiTenantConnectionProvider multiTenantConnectionProvider() throws URISyntaxException {
        String globalSchema = env.getProperty(GLOBAL_SCHEMA_KEY);
        return new PostgresSchemaMultiTenantConnectionProvider(dataSource(), globalSchema);
    }

    @Bean
    public CurrentTenantIdentifierResolver tenantIdentifierResolver() {
        Supplier<String> tenantResolver = () -> { return getTenantFromSession(); };
        return new UserSessionCurrentTenantIdentifierResolver(tenantResolver);
    }

    private String getTenantFromSession() {
        // Because of AnonymousAuthenticationFilter, there will always be a Principal
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();
        if (principal.equals(ANONYMOUS_USER)) {
            return env.getProperty(GLOBAL_SCHEMA_KEY);
        }
        return ((EnrichedUserDetails) principal).getTenant();
    }


    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() throws URISyntaxException {

        LocalContainerEntityManagerFactoryBean emfBean = new LocalContainerEntityManagerFactoryBean();
        emfBean.setDataSource(dataSource());
        emfBean.setPackagesToScan(AssetManagerApplication.class.getPackage().getName());
        emfBean.setJpaVendorAdapter(jpaVendorAdapter());

        Map<String, Object> jpaProperties = new HashMap<>();
        jpaProperties.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");

        // Improved naming strategy deprecated as of hibernate 5.0
        // jpaProperties.put("hibernate.ejb.naming_strategy", "org.hibernate.cfg.ImprovedNamingStrategy");
        jpaProperties.put("hibernate.implicit_naming_strategy", "org.hibernate.boot.model.naming.ImplicitNamingStrategyLegacyHbmImpl");
        jpaProperties.put("hibernate.physical_naming_strategy", "com.accounted4.commons.db.HibernateLegacyImprovedNamingStrategy");

        jpaProperties.put("hibernate.show_sql", "true");
        jpaProperties.put("hibernate.format_sql", "true");
        jpaProperties.put(org.hibernate.cfg.Environment.MULTI_TENANT, MultiTenancyStrategy.SCHEMA);
        jpaProperties.put(org.hibernate.cfg.Environment.MULTI_TENANT_CONNECTION_PROVIDER, multiTenantConnectionProvider());
        jpaProperties.put(org.hibernate.cfg.Environment.MULTI_TENANT_IDENTIFIER_RESOLVER, tenantIdentifierResolver());
        emfBean.setJpaPropertyMap(jpaProperties);
        return emfBean;
    }


}
