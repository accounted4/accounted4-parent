package com.accounted4.commons.db;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

import java.net.URISyntaxException;
import java.sql.SQLException;
import static org.hamcrest.CoreMatchers.not;
import org.junit.Rule;

import org.junit.Test;
import org.junit.contrib.java.lang.system.EnvironmentVariables;

public class PostgresConnectConfigTest {


    private static final String  DB_URL_TEMPLATE = "postgres://%s:%s@%s:%s/%s?%s";
    private static final String  DB_URL_TEMPLATE_WITH_NO_QUERY = "postgres://%s:%s@%s:%s/%s";

    private static final String USER_NAME = "user1";
    private static final String PASSWORD = "password1";
    private static final String HOST = "host1";
    private static final String PORT = "8081";
    private static final String DB_NAME = "testdb";
    private static final String QUERY = "ssl=true&sslfactory=org.postgresql.ssl.NonValidatingFactory";

    private static final String DB_URL = String.format(DB_URL_TEMPLATE, USER_NAME, PASSWORD, HOST, PORT, DB_NAME, QUERY);
    private static final String DB_URL_WITH_NO_QUERY = String.format(DB_URL_TEMPLATE_WITH_NO_QUERY, USER_NAME, PASSWORD, HOST, PORT, DB_NAME);
    private static final String DB_URL_PROPERTY_NAME = "DATABASE_URL";


    @Rule
    public final EnvironmentVariables environmentVariables = new EnvironmentVariables();

    @Test
    public void connectConfigPropertisMatchSuppliedDbUrlProperties() throws URISyntaxException, SQLException {
        PostgresConnectConfig config = new PostgresConnectConfig(DB_URL);
        assertThat(USER_NAME, is(equalTo(config.getUsername())));
        assertThat(PASSWORD, is(equalTo(config.getPassword())));
        String expectedDbUrl = String.format("jdbc:postgresql://%s:%s/%s?%s", HOST, PORT, DB_NAME, QUERY);
        assertThat(expectedDbUrl, is(equalTo(config.getDbUrl())));
    }

    @Test
    public void connectConfigPropertiesExtractedFromEnvironmentMatchSuppliedDbUrlProperties()
            throws URISyntaxException, SQLException {

        environmentVariables.set(DB_URL_PROPERTY_NAME, DB_URL);
        PostgresConnectConfig config = new PostgresConnectConfig();
        assertThat(USER_NAME, is(equalTo(config.getUsername())));
        assertThat(PASSWORD, is(equalTo(config.getPassword())));
        String expectedDbUrl = String.format("jdbc:postgresql://%s:%s/%s?%s", HOST, PORT, DB_NAME, QUERY);
        assertThat(expectedDbUrl, is(equalTo(config.getDbUrl())));

    }

    @Test
    public void connectConfigPropertisMatchSuppliedDbUrlWithNoQueryProperties() throws URISyntaxException, SQLException {
        PostgresConnectConfig config = new PostgresConnectConfig(DB_URL_WITH_NO_QUERY);
        assertThat(USER_NAME, is(equalTo(config.getUsername())));
        assertThat(PASSWORD, is(equalTo(config.getPassword())));
        String expectedDbUrl = String.format("jdbc:postgresql://%s:%s/%s", HOST, PORT, DB_NAME);
        assertThat(expectedDbUrl, is(equalTo(config.getDbUrl())));
    }

    @Test
    public void connectConfigEqualityWhenPasswordsDiffer() throws URISyntaxException, SQLException {

        String uri1 = String.format(DB_URL_TEMPLATE_WITH_NO_QUERY, USER_NAME, "password1", "a", "1", "b");
        String uri2 = String.format(DB_URL_TEMPLATE_WITH_NO_QUERY, USER_NAME, "password2", "a", "1", "b");

        PostgresConnectConfig config1 = new PostgresConnectConfig(uri1);
        PostgresConnectConfig config2 = new PostgresConnectConfig(uri2);

        assertThat("Password difference does not affect equality", config1, is(equalTo(config2)));
        assertThat("Password difference does not affect equality on presentation output", config1.toString(), is(equalTo(config2.toString())));
        assertThat("Presentation output", config1.toString(), is(equalTo(USER_NAME + " @ " + config1.getDbUrl())));

    }

    @Test
    public void connectConfigEqualityMustHaveSameUsername() throws URISyntaxException, SQLException {

        String uri1 = String.format(DB_URL_TEMPLATE_WITH_NO_QUERY, "a", "password1", "a", "1", "b");
        String uri2 = String.format(DB_URL_TEMPLATE_WITH_NO_QUERY, "b", "password1", "a", "1", "b");

        PostgresConnectConfig config1 = new PostgresConnectConfig(uri1);
        PostgresConnectConfig config2 = new PostgresConnectConfig(uri2);

        assertThat("Username difference fails equality", config1, is(not(equalTo(config2))));
    }

    @Test
    public void connectConfigEqualityMustHaveSameUrl() throws URISyntaxException, SQLException {

        String uri1 = String.format(DB_URL_TEMPLATE_WITH_NO_QUERY, "a", "password1", "a", "1", "b");
        String uri2 = String.format(DB_URL_TEMPLATE_WITH_NO_QUERY, "a", "password1", "b", "1", "b");

        PostgresConnectConfig config1 = new PostgresConnectConfig(uri1);
        PostgresConnectConfig config2 = new PostgresConnectConfig(uri2);

        assertThat("Host difference fails equality", config1, is(not(equalTo(config2))));

        uri2 = String.format(DB_URL_TEMPLATE_WITH_NO_QUERY, "a", "password1", "a", "2", "b");
        config2 = new PostgresConnectConfig(uri2);
        assertThat("Port difference fails equality", config1, is(not(equalTo(config2))));

        uri2 = String.format(DB_URL_TEMPLATE_WITH_NO_QUERY, "a", "password1", "a", "1", "c");
        config2 = new PostgresConnectConfig(uri2);
        assertThat("DbName difference fails equality", config1, is(not(equalTo(config2))));

    }

}
