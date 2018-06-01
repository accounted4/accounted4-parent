package com.accounted4.commons.db;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;

import org.junit.Test;


/**
 *
 */
public class HibernateLegacyImprovedNamingStrategyTest {


    static final HibernateLegacyImprovedNamingStrategy NAMING_STRATEGY = new HibernateLegacyImprovedNamingStrategy();
    static final JdbcEnvironment JDBC_ENVIRONMENT = null;

    private enum DbAttribute {
        TABLE, COLUMN;
    }


    @Test
    public void whenLogicalNameHasNoUpperCaseThenPhysicalTableNameIsSimplyLowerCase() {

        final String logicalName = "nocamel";
        final String expectedPhysicalName = logicalName.toLowerCase();
        final String actualPhysicalName = getPhysicalName(logicalName, DbAttribute.TABLE);

        assertEquality(logicalName, actualPhysicalName, expectedPhysicalName);
    }


    @Test
    public void whenLogicalNameHasAllUpperCaseThenPhysicalTableNameIsSimplyLowerCase() {

        final String logicalName = "ALLUPPER";
        final String expectedPhysicalName = logicalName.toLowerCase();
        final String actualPhysicalName = getPhysicalName(logicalName, DbAttribute.TABLE);

        assertEquality(logicalName, actualPhysicalName, expectedPhysicalName);
    }


    @Test
    public void whenLogicalNameStartsWithUpperCaseThenPhysicalTableNameDoesNotStartWithUnderscore() {

        final String logicalName = "Nocamel";
        final String expectedPhysicalName = logicalName.toLowerCase();
        final String actualPhysicalName = getPhysicalName(logicalName, DbAttribute.TABLE);

        assertEquality(logicalName, actualPhysicalName, expectedPhysicalName);
    }


    @Test
    public void whenLogicalNameHasUpperCaseThenPhysicalTableNameInsertsUnderscores() {

        final String logicalName = "camelCaseInput";
        final String expectedPhysicalName = "camel_case_input";
        final String actualPhysicalName = getPhysicalName(logicalName, DbAttribute.TABLE);

        assertEquality(logicalName, actualPhysicalName, expectedPhysicalName);
    }


    @Test
    public void whenLogicalNameHasPeriodThenPhysicalTableNameTreatsPeriodAsUnderscores() {

        final String logicalName = "schema.camelCaseInput";
        final String expectedPhysicalName = "schema_camel_case_input";
        final String actualPhysicalName = getPhysicalName(logicalName, DbAttribute.TABLE);

        assertEquality(logicalName, actualPhysicalName, expectedPhysicalName);
    }


    @Test
    public void whenLogicalNameEndsWithUpperCaseThenPhysicalTableNameHasUnderscore() {

        final String logicalName = "camelCaseInputA";
        final String expectedPhysicalName = "camel_case_input_a";
        final String actualPhysicalName = getPhysicalName(logicalName, DbAttribute.TABLE);

        assertEquality(logicalName, actualPhysicalName, expectedPhysicalName);
    }


    @Test
    public void whenLogicalNameHasConsecutiveUpperCaseThenPhysicalTableNameTreatsThatAsAUnit() {

        final String logicalName = "camelCaseHTTPInput";
        final String expectedPhysicalName = "camel_case_http_input";
        final String actualPhysicalName = getPhysicalName(logicalName, DbAttribute.TABLE);

        assertEquality(logicalName, actualPhysicalName, expectedPhysicalName);
    }


    @Test
    public void whenLogicalNameStartsWithConsecutiveUpperCaseThenPhysicalTableNameTreatsThatAsAUnit() {

        final String logicalName = "ABCDefGhi";
        final String expectedPhysicalName = "abc_def_ghi";
        final String actualPhysicalName = getPhysicalName(logicalName, DbAttribute.TABLE);

        assertEquality(logicalName, actualPhysicalName, expectedPhysicalName);
    }


    @Test
    public void whenLogicalNameEndsWithConsecutiveUpperCaseThenPhysicalTableNameTreatsThatAsAUnit() {

        final String logicalName = "abcDefGHI";
        final String expectedPhysicalName = "abc_def_ghi";
        final String actualPhysicalName = getPhysicalName(logicalName, DbAttribute.TABLE);

        assertEquality(logicalName, actualPhysicalName, expectedPhysicalName);
    }


    @Test
    public void whenLogicalNameHasNoUpperCaseThenPhysicalColumnNameIsSimplyLowerCase() {

        final String logicalName = "nocamel";
        final String expectedPhysicalName = logicalName.toLowerCase();
        final String actualPhysicalName = getPhysicalName(logicalName, DbAttribute.COLUMN);

        assertEquality(logicalName, actualPhysicalName, expectedPhysicalName);
    }


    @Test
    public void whenLogicalNameStartsWithUpperCaseThenPhysicalColumnNameDoesNotStartWithUnderscore() {

        final String logicalName = "Nocamel";
        final String expectedPhysicalName = logicalName.toLowerCase();
        final String actualPhysicalName = getPhysicalName(logicalName, DbAttribute.COLUMN);

        assertEquality(logicalName, actualPhysicalName, expectedPhysicalName);
    }


    @Test
    public void whenLogicalNameHasUpperCaseThenPhysicalColumnNameInsertsUnderscores() {

        final String logicalName = "camelCaseInput";
        final String expectedPhysicalName = "camel_case_input";
        final String actualPhysicalName = getPhysicalName(logicalName, DbAttribute.COLUMN);

        assertEquality(logicalName, actualPhysicalName, expectedPhysicalName);
    }


    @Test
    public void whenLogicalNameHasUpperCaseGroupingThenPhysicalColumnNameDetectsLastChar() {

        final String logicalName = "HTTPConnection";
        final String expectedPhysicalName = "http_connection";
        final String actualPhysicalName = getPhysicalName(logicalName, DbAttribute.COLUMN);

        assertEquality(logicalName, actualPhysicalName, expectedPhysicalName);
    }


    @Test
    public void whenLogicalNameIsNullOrEmptyThenResultIsEmpty() {

        final String expectedPhysicalName = "";

        String logicalName = "";
        String actualPhysicalName = getPhysicalName(logicalName, DbAttribute.COLUMN);
        assertEquality(logicalName, actualPhysicalName, expectedPhysicalName);

        logicalName = null;
        actualPhysicalName = getPhysicalName(logicalName, DbAttribute.COLUMN);
        assertEquality(logicalName, actualPhysicalName, expectedPhysicalName);

    }

    @Test
    public void whenLogicalNameIsSingleCharacterThenResultIsLowercase() {

        final String expectedPhysicalName = "a";

        String logicalName = "A";
        String actualPhysicalName = getPhysicalName(logicalName, DbAttribute.COLUMN);
        assertEquality(logicalName, actualPhysicalName, expectedPhysicalName);

        logicalName = "A";
        actualPhysicalName = getPhysicalName(logicalName, DbAttribute.COLUMN);
        assertEquality(logicalName, actualPhysicalName, expectedPhysicalName);

    }

    @Test
    public void whenLogicalNameIsTwoCharacters() {

        String logicalName = "aB";
        String expectedPhysicalName = "a_b";
        String actualPhysicalName = getPhysicalName(logicalName, DbAttribute.COLUMN);
        assertEquality(logicalName, actualPhysicalName, expectedPhysicalName);

        logicalName = "Ab";
        expectedPhysicalName = "ab";
        actualPhysicalName = getPhysicalName(logicalName, DbAttribute.COLUMN);
        assertEquality(logicalName, actualPhysicalName, expectedPhysicalName);

        logicalName = "AB";
        expectedPhysicalName = "ab";
        actualPhysicalName = getPhysicalName(logicalName, DbAttribute.COLUMN);
        assertEquality(logicalName, actualPhysicalName, expectedPhysicalName);

        logicalName = "ab";
        expectedPhysicalName = "ab";
        actualPhysicalName = getPhysicalName(logicalName, DbAttribute.COLUMN);
        assertEquality(logicalName, actualPhysicalName, expectedPhysicalName);
        
    }


    private static void assertEquality(String logicalName, String actualPhysicalName, String expectedPhysicalName) {
        assertThat(String.format("Given: %s ", logicalName), actualPhysicalName, is(equalTo(expectedPhysicalName)));
    }


    private String getPhysicalName(String logicalName, DbAttribute dbAttribute) {
        final Identifier identifier = Identifier.toIdentifier(logicalName);
        final Identifier actualPhysicalName =
                (dbAttribute == DbAttribute.TABLE ?
                        NAMING_STRATEGY.toPhysicalTableName(identifier, JDBC_ENVIRONMENT) :
                        NAMING_STRATEGY.toPhysicalColumnName(identifier, JDBC_ENVIRONMENT)
                );
        return null == actualPhysicalName ? "" : actualPhysicalName.getText();
    }

}
