package com.accounted4.commons.db;

import java.util.function.Supplier;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.core.Is.is;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author gheinze
 */
public class UserSessionCurrentTenantIdentifierResolverTest {


    @Test
    public void testResolveCurrentTenantIdentifier() {

        final String expectedResult = "testTenant";

        Supplier<String> tenantResolver = () -> { return expectedResult; };

        UserSessionCurrentTenantIdentifierResolver instance = new UserSessionCurrentTenantIdentifierResolver(tenantResolver);
        String result = instance.resolveCurrentTenantIdentifier();
        assertThat("Tenant resolver", result, is(equalTo(expectedResult)));
        assertEquals(expectedResult, result);
    }

    @Test
    public void testValidateExistingCurrentSessions() {
        UserSessionCurrentTenantIdentifierResolver instance = new UserSessionCurrentTenantIdentifierResolver(null);
        boolean expResult = true;
        boolean result = instance.validateExistingCurrentSessions();
        assertEquals(expResult, result);
    }

}
