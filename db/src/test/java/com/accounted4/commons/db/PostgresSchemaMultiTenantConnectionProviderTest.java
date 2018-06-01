package com.accounted4.commons.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import javax.sql.DataSource;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.core.Is.is;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 *
 * @author gheinze
 */
public class PostgresSchemaMultiTenantConnectionProviderTest {

    private static final String DEFAULT_TENANT = "testDefaultTenant";

    @Test
    public void testThatIsUnwrappableAsReturnsFalse() {
        PostgresSchemaMultiTenantConnectionProvider instance = new PostgresSchemaMultiTenantConnectionProvider(null, DEFAULT_TENANT);
        boolean expResult = false;
        boolean result = instance.isUnwrappableAs(PostgresSchemaMultiTenantConnectionProviderTest.class);
        assertThat(expResult, is(equalTo(result)));
    }


    @Test
    public void testThatUnwrapReturnsNull() {
        PostgresSchemaMultiTenantConnectionProvider instance = new PostgresSchemaMultiTenantConnectionProvider(null, DEFAULT_TENANT);
        PostgresSchemaMultiTenantConnectionProviderTest result = instance.unwrap(PostgresSchemaMultiTenantConnectionProviderTest.class);
        assertNull(result);
    }


    @Test
    public void testThatGetAnyConnectionReturnsConnectionFromDataSource() throws Exception {

        PreparedStatement mockPstmt = mock(PreparedStatement.class);
        doNothing().when(mockPstmt).setString(eq(1), eq(DEFAULT_TENANT));
        when(mockPstmt.execute()).thenReturn(true);

        Connection expectedConnection = mock(Connection.class);
        when(expectedConnection.prepareStatement(anyString())).thenReturn(mockPstmt);

        DataSource ds = mock(DataSource.class);
        when(ds.getConnection()).thenReturn(expectedConnection);

        PostgresSchemaMultiTenantConnectionProvider instance = new PostgresSchemaMultiTenantConnectionProvider(ds, DEFAULT_TENANT);
        Connection retrievedConnection = instance.getAnyConnection();
        assertThat(expectedConnection, is(equalTo(retrievedConnection)));
    }


    @Test
    public void testThatReleaseAnyConnectionResetsConnectionToDefaultSchema() throws Exception {

        PostgresSchemaMultiTenantConnectionProvider instance = new PostgresSchemaMultiTenantConnectionProvider(null, DEFAULT_TENANT);

        PreparedStatement mockPstmt = mock(PreparedStatement.class);
        doNothing().when(mockPstmt).setString(eq(1), eq(DEFAULT_TENANT));
        when(mockPstmt.execute()).thenReturn(true);

        Connection mockConnection = mock(Connection.class);
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPstmt);

        instance.releaseAnyConnection(mockConnection);

        verify(mockPstmt, times(1)).execute();
    }

    @Test
    public void testThatGetSetsSchema() throws Exception {

        PreparedStatement mockPstmt = mock(PreparedStatement.class);
        doNothing().when(mockPstmt).setString(eq(1), eq(DEFAULT_TENANT));
        when(mockPstmt.execute()).thenReturn(true);

        Connection mockConnection = mock(Connection.class);
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPstmt);

        DataSource ds = mock(DataSource.class);
        when(ds.getConnection()).thenReturn(mockConnection);

        PostgresSchemaMultiTenantConnectionProvider instance = new PostgresSchemaMultiTenantConnectionProvider(ds, DEFAULT_TENANT);

        instance.getConnection(DEFAULT_TENANT);

        // Two times: once for getAnyConnection that sets to default, second time for getConnection that sets to schema
        verify(mockPstmt, times(2)).execute();
    }


    @Test
    public void testReleaseConnectionCallsReleaseAnyConnection() throws Exception {
        PostgresSchemaMultiTenantConnectionProvider instance = new PostgresSchemaMultiTenantConnectionProvider(null, DEFAULT_TENANT);

        PreparedStatement mockPstmt = mock(PreparedStatement.class);
        doNothing().when(mockPstmt).setString(anyInt(), anyString());
        when(mockPstmt.execute()).thenReturn(true);

        Connection mockConnection = mock(Connection.class);
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPstmt);

        instance.releaseConnection(DEFAULT_TENANT, mockConnection);

        verify(mockPstmt, times(1)).execute();
    }


    @Test
    public void testSupportsAggressiveReleaseReturnsTrue() {
        PostgresSchemaMultiTenantConnectionProvider instance = new PostgresSchemaMultiTenantConnectionProvider(null, DEFAULT_TENANT);
        boolean expResult = true;
        boolean result = instance.supportsAggressiveRelease();
        assertThat(expResult, is(equalTo(result)));
    }

}
