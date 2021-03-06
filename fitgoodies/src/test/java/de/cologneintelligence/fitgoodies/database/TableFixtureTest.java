/*
 * Copyright (c) 2009-2012  Cologne Intelligence GmbH
 * This file is part of FitGoodies.
 *
 * FitGoodies is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FitGoodies is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with FitGoodies.  If not, see <http://www.gnu.org/licenses/>.
 */


package de.cologneintelligence.fitgoodies.database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.jmock.Expectations;
import org.jmock.Mockery;

import de.cologneintelligence.fitgoodies.FitGoodiesTestCase;
import de.cologneintelligence.fitgoodies.ScientificDouble;
import de.cologneintelligence.fitgoodies.util.DependencyManager;
import fit.Parse;

/**
 *
 * @author jwierum
 */
public class TableFixtureTest extends FitGoodiesTestCase {
    private TableFixture fixture;
    private SetupHelper helper;

    @Override
    public final void setUp() throws Exception {
        super.setUp();
        helper = DependencyManager.getOrCreate(SetupHelper.class);
        helper.setUser("user");
        helper.setPassword("pw");
        SetupHelper.setProvider("de.cologneintelligence.fitgoodies.database.DriverMock");
        helper.setConnectionString("jdbc://test");

        fixture = new TableFixture();
    }

    public final void testGetResultSet() throws SQLException {
        final Connection conn = mock(Connection.class);
        final Statement statement = mock(Statement.class);
        final ResultSet resultSet = mock(ResultSet.class);

        checking(new Expectations() {{
            oneOf(conn).createStatement(); will(returnValue(statement));
            oneOf(statement).executeQuery("SELECT * FROM table");
            will(returnValue(resultSet));
        }});

        fixture.setConnection(conn);
        final ResultSet actual = fixture.getResultSet("table", null);

        assertSame(resultSet, actual);
    }

    public final void testGetResultSetWithWhere() throws SQLException {
        final Connection conn = mock(Connection.class);
        final Statement statement = mock(Statement.class);
        final ResultSet resultSet = mock(ResultSet.class);

        checking(new Expectations() {{
            oneOf(conn).createStatement(); will(returnValue(statement));
            oneOf(statement).executeQuery("SELECT * FROM tbl2 WHERE x > 3");
            will(returnValue(resultSet));
        }});

        fixture.setConnection(conn);
        final ResultSet actual = fixture.getResultSet("tbl2", "x > 3");

        assertSame(resultSet, actual);
    }

    public final void testDoTable() throws Exception {
        final Parse table = new Parse(
                "<table>"
                        + "<tr><td>ignore</td></tr>"
                        + "<tr><td>name</td><td>age</td></tr>"
                        + "<tr><td>Anika Hanson</td><td>30</td></tr>"
                        + "</table>");

        final Mockery context = new Mockery();
        fixture.setParams(new String[]{"table=tbl7"});
        fixture.setConnection(ResultSetMockGenerator.mkConnection(context,
                "tbl7",
                new String[]{"name", "age"},
                new Object[][]{
                new Object[] {"Angela Bennett", 32},
                new Object[] {"Anika Hanson", 30}
        }
                ));
        fixture.doTable(table);
        assertEquals("tbl7", fixture.getTable());

        final Class<?> c = fixture.getTargetClass();

        assertSame(String.class, c.getField("name").getType());
        assertSame(Integer.class, c.getField("age").getType());

        assertEquals(1, fixture.counts.wrong);
        assertEquals(2, fixture.counts.right);

        context.assertIsSatisfied();
    }

    public final void testDoTable2() throws Exception {
        final Parse table = new Parse(
                "<table>"
                        + "<tr><td>ignore</td></tr>"
                        + "<tr><td>age</td></tr>"
                        + "<tr><td>42.3</td></tr>"
                        + "</table>");

        final Mockery context = new Mockery();
        fixture.setParams(new String[]{"table=table1"});
        fixture.setConnection(ResultSetMockGenerator.mkConnection(context,
                "table1",
                new String[]{"age"},
                new Object[][]{
                new Object[] {42.3}
        }
                ));
        fixture.doTable(table);
        assertEquals("table1", fixture.getTable());

        final Class<?> c = fixture.getTargetClass();
        assertSame(ScientificDouble.class, c.getField("age").getType());
        assertEquals(1, fixture.counts.right);
        context.assertIsSatisfied();
    }

    public final void testConstructor() {
        final Mockery context = new Mockery();
        final Connection conn = (ResultSetMockGenerator.mkConnection(context,
                null,
                new String[]{},
                new Object[][]{}
                ));

        fixture = new TableFixture(conn);
        assertSame(conn, fixture.getConnection());
    }

    public final void testHelperInteraction() throws Exception {
        assertSame(helper.getConnection(), fixture.getConnection());
        DriverMock.cleanup();
    }

    public final void testWhereClause() throws Exception {
        final Parse table = new Parse(
                "<table>"
                        + "<tr><td>ignore</td></tr>"
                        + "<tr><td>x</td></tr>"
                        + "</table>");

        final Mockery context = new Mockery();
        final Connection conn = (ResultSetMockGenerator.mkConnection(context,
                "tbl4", "x > 7",
                new String[]{},
                new Object[][]{}
                ));

        fixture.setConnection(conn);
        fixture.setParams(new String[]{"table=tbl4", "where=x > 7"});
        fixture.doTable(table);
        fixture.getConnection();
        context.assertIsSatisfied();
    }

    public final void testError() throws Exception {
        final Parse table = new Parse(
                "<table>"
                        + "<tr><td>ignore</td></tr>"
                        + "<tr><td>x</td></tr>"
                        + "</table>");
        fixture.doTable(table);
        assertEquals(1, fixture.counts.exceptions);
    }
}
