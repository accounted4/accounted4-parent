package com.accounted4.am.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.transaction.annotation.EnableTransactionManagement;


@Configuration
@EnableJpaRepositories(
        entityManagerFactoryRef = "multiTenantEntityManager",
//        transactionManagerRef = "serversTransactionManager",
        basePackages = {"com.accounted4.am.feature"}        
)
@EnableTransactionManagement
@EnableSpringDataWebSupport
public class AssetManagerConfig {


}
