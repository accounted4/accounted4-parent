package com.accounted4.commons.db;

import java.util.function.Supplier;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;


/**
 * A callback for Hibernate to determine which tenant to use.
 *
 * @author gheinze
 *
 */
public class UserSessionCurrentTenantIdentifierResolver implements CurrentTenantIdentifierResolver {

    private final Supplier<String> tenantResolver;


    /**
     *
     * @param tenantResolver the code fragment to retrieve a the tenant name, typically pulled
     * out of session.
     */
    public UserSessionCurrentTenantIdentifierResolver(final Supplier<String> tenantResolver) {
        this.tenantResolver = tenantResolver;
    }

    @Override
    public String resolveCurrentTenantIdentifier() {
        return tenantResolver.get();
    }

    @Override
    public boolean validateExistingCurrentSessions() {
        return true;
    }

}
