package com.accounted4.commons.db;

import java.util.Locale;

import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;

/**
 * A Hibernate "Physical Naming Strategy" for converting entity names written in traditional Java style
 * (camel case) into a traditional database style (snake case). For example, the entity name "myTable"
 * used in Java may refer to the table name "my_table".
 *
 * "ImprovedNamingStrategy" previously provided by Hibernate has been deprecated with Hibernate 5.0.
 * Hibernate 5.0 introduced a split into a "physical" naming strategy and an "implicit" naming strategy.
 * See: <a href="https://hibernate.atlassian.net/browse/HHH-9417">https://hibernate.atlassian.net/browse/HHH-9417</a>
 * <p>
 * <pre>
 *   The logical name is the name used to register tables/columns for lookup.
 *   The implicit name is the name determined when one is not explicitly specified.
 *   The physical name is the name we ultimately use with the database.
 *   Implicit and physical naming should be controllable via pluggable "naming strategies".
 *   Logical name probably should not be pluggable as it is an internal implementation detail
 *   (more or less a Map key) and things will break down if this is not done properly and consistently on both sides.
 * </pre>
 *
 * This class attempts to create a physical naming strategy with behaviour similar to the legacy
 * ImprovedNamingStrategy until something better comes along.
 *
 * See:
 * https://github.com/spring-projects/spring-boot/issues/2763
 * http://stackoverflow.com/questions/32437202/hibernate-naming-strategy-not-working
 *
 * @author gheinze
 */

public class HibernateLegacyImprovedNamingStrategy extends PhysicalNamingStrategyStandardImpl {

    private static final long serialVersionUID = 1L;


    @Override
    public Identifier toPhysicalTableName(final Identifier idntfr, final JdbcEnvironment je) {
        return null == idntfr ? null : new Identifier(toSnakeCase(idntfr.getText()), idntfr.isQuoted());
    }

    @Override
    public Identifier toPhysicalColumnName(final Identifier idntfr, final JdbcEnvironment je) {
        return null == idntfr ? null : new Identifier(toSnakeCase(idntfr.getText()), idntfr.isQuoted());
    }



    private static final String UNDERSCORE = "_";

    /*
     * Uppercase characters signal the start of a new word if immediately preceded or
     * immediately followd by a lowercase letter. Words are separated with an underscore,
     * then the entire sequence is converted to lowercase.
     * Consecutive uppercase characters are treated as a unit.
     * Null or empty input results in an empty string.
     */
    private static String toSnakeCase(final String inputName) {

        if (null == inputName || inputName.isEmpty()) {
            return "";
        }

        if (1 == inputName.length()) {
            return inputName.toLowerCase(Locale.ROOT);
        }

        final StringBuilder buffer = new StringBuilder();

        for (int i = 0; i < inputName.length(); i++) {

            char currentChar = inputName.charAt(i);

            if (Character.isUpperCase(currentChar)
                    && (lookBackIsLower(inputName, i) || lookAheadIsLower(inputName, i))) {
                buffer.append(UNDERSCORE);
            }

            buffer.append(currentChar);

        }

        return buffer.toString().toLowerCase(Locale.ROOT).replace('.', '_');
    }


    private static boolean lookBackIsLower(final String name, final int i) {
        assert name.length() > 1;
        if (i >= 1 && i < name.length()) {
            return !Character.isUpperCase(name.charAt(i - 1));
        }
        return false;
    }


    private static boolean lookAheadIsLower(final String name, final int i) {
        assert name.length() > 1;
        if (i > 0 && i < name.length() - 1) {
            return !Character.isUpperCase(name.charAt(i + 1));
        }
        return false;
    }


}
