package com.accounted4.commons.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.hibernate.HibernateException;
import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;

import lombok.extern.slf4j.Slf4j;

/**
 * An implementation of MultiTenantConnectionProvider which supplies Hibernate with a DataSource
 * object configured to use the specified tenant. The implementation uses the tenant to
 * identify the schema to be used for the operation. Schema usage is controlled by
 * setting the SEARCH_PATH=tenant.
 *
 * @author gheinze
 */
@Slf4j
public class PostgresSchemaMultiTenantConnectionProvider implements MultiTenantConnectionProvider {


    private static final long serialVersionUID = 1L;


    private final transient DataSource dataSource;
    private final String defaultTenant;


    /**
     * Creates the Postgres schema based DataSource provider.
     *
     * @param dataSource the DataSource/ConnectionPool to use for database communication.
     * @param defaultTenant  the tenant to use if the requested tenant is not supplied.
     */
    public PostgresSchemaMultiTenantConnectionProvider(final DataSource dataSource, final String defaultTenant) {
        this.dataSource = dataSource;
        this.defaultTenant = defaultTenant;
    }


    @Override
    public boolean isUnwrappableAs(@SuppressWarnings("rawtypes") final Class unwrapType) {
        return false;
    }

    @Override
    public <T> T unwrap(final Class<T> unwrapType) {
        return null;
    }

    @Override
    public Connection getAnyConnection() throws SQLException {
        final Connection connection = dataSource.getConnection();
        // This is not strictly necessary and is overridden by getConnection.
        // The intention is to be safe: never return a connecton that may have
        // a lingering configuration to some other tenant.
        setSchemaTo(connection, defaultTenant);
        return connection;
    }

    @Override
    public void releaseAnyConnection(final Connection connection) throws SQLException {
        try {
            setSchemaTo(connection, defaultTenant);
        } catch (HibernateException he) {
            log.warn("Failed to revert schema for connection during connection close", he);
        }
        connection.close();
    }

    @Override
    public Connection getConnection(final String tenantIdentifier) throws SQLException {
        final Connection connection = getAnyConnection();
        setSchemaTo(connection, tenantIdentifier);
        return connection;
    }

    @Override
    public void releaseConnection(final String tenantIdentifier, final Connection connection) throws SQLException {
        releaseAnyConnection(connection);
    }

    @Override
    public boolean supportsAggressiveRelease() {
        return true;
    }



    private static final String SET_SCHEMA_TO = "SELECT public.set_schema_to(?)";

    private void setSchemaTo(final Connection connection, final String tenantIdentifier) {
        try (PreparedStatement pStmt = connection.prepareStatement(SET_SCHEMA_TO)) {
            pStmt.setString(1, tenantIdentifier);
            pStmt.execute();
        } catch (SQLException ex) {
            throw new HibernateException(
                    String.format("Could not alter JDBC connection to specified schema [%s]", tenantIdentifier),
                    ex
            );
        }
    }

}
