package com.accounted4.commons.db;

import java.net.URI;
import java.net.URISyntaxException;

import lombok.Data;
import lombok.EqualsAndHashCode;

import org.apache.commons.lang3.StringUtils;


/**
 * Extract database connection information from a db connect string. The db connect string is
 * expected to be of the form:
 * <p>
 * postgres://&lt;username&gt;:&lt;password&gt;&#064;&lt;host&gt;:&lt;port&gt;/&lt;dbname&gt;
 * <p>
 * Default creation will assume the db connect string can be extracted as a system environment
 * variable named "DATABASE_URL" (note: system environment variable, not Java property).
 * This is the same key name used by Heroku to pass db information to an application.
 * <p>
 * Note also: query suffixes may be added to the db connect string. For example, remote connections
 * to Heroku require ssl support, which adds the following URI query to the db connect string:
 * <p>
 * ?ssl=true&amp;sslfactory=org.postgresql.ssl.NonValidatingFactory
 * <p>
 * In this case the DATABASE_URL for Heroku would have the following format:
 * <p>
postgres://&lt;username&gt;:&lt;password&gt;&#064;&lt;host&gt;:&lt;port&gt;/&lt;dbname&gt;
?ssl=true&amp;sslfactory=org.postgresql.ssl.NonValidatingFactory
 * <p>
 *
 * @author gheinze
 *
 */
@Data
@EqualsAndHashCode(exclude = {"password"})
public class PostgresConnectConfig {

    private static final String SYSTEM_ENVIRONMENT_DATABASE_URL_KEY = "DATABASE_URL";
    private static final int INITIAL_URL_BUILDER_BUFFER_SIZE = 64;

    private final String username;
    private final String password;
    private final String dbUrl;


    /**
     * Generate a data object with db connection properties extracted from a system environment variable
     * named DATABASE_URL. The expected format of the variable is:
     *
     * postgres://&lt;username&gt;:&lt;password&gt;&#064;&lt;host&gt;:&lt;port&gt;/&lt;dbname&gt;
     *
     * @throws URISyntaxException if the dbDescriptor is not parsable as a uri string
     */
    public PostgresConnectConfig() throws URISyntaxException {
        this(System.getenv(SYSTEM_ENVIRONMENT_DATABASE_URL_KEY));
    }


    /**
     * Generate a data object with db connection properties extracted from provided parameter.
     *
     * @param dbDescriptor a string in uri format containing db connection details.  Expected format:
     *      postgres://&lt;username&gt;:&lt;password&gt;&#064;&lt;host&gt;:&lt;port&gt;/&lt;dbname&gt;
     *
     * @throws URISyntaxException if the dbDescriptor is not parsable as a uri string
     */
    public PostgresConnectConfig(final String dbDescriptor) throws URISyntaxException {

        URI dbUri = new URI(dbDescriptor);

        this.username = dbUri.getUserInfo().split(":")[0];
        this.password = dbUri.getUserInfo().split(":")[1];

        StringBuilder dbUrlBuilder = new StringBuilder(INITIAL_URL_BUILDER_BUFFER_SIZE);
        dbUrlBuilder.append("jdbc:postgresql://")
                .append(dbUri.getHost()).append(":")
                .append(dbUri.getPort())
                .append(dbUri.getPath());

        String query = dbUri.getQuery();
        if (!StringUtils.isEmpty(query)) {
            dbUrlBuilder.append("?").append(query);
        }

        this.dbUrl = dbUrlBuilder.toString();

    }


    @Override
    public String toString() {
        return String.format("%s @ %s", username, dbUrl);
    }


}
