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
import java.sql.DriverManager;

import de.cologneintelligence.fitgoodies.FitGoodiesTestCase;


/**
 *
 * @author jwierum
 */
public class SetupHelperTest extends FitGoodiesTestCase {
    private SetupHelper helper;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        helper = new SetupHelper();
    }

    public final void testProvider() throws Exception {
        SetupHelper.setProvider("de.cologneintelligence.fitgoodies.database.DriverMock");
        assertNotNull(DriverManager.getDriver("jdbc://test"));
    }

    public final void testUser() {
        helper.setUser("test");
        assertEquals("test", helper.getUser());
        helper.setUser("user");
        assertEquals("user", helper.getUser());
    }

    public final void testPassword() {
        helper.setPassword("pass");
        assertEquals("pass", helper.getPassword());
        helper.setPassword("pw2");
        assertEquals("pw2", helper.getPassword());
    }

    public final void testSetConnectionString() {
        helper.setConnectionString("text");
        assertEquals("text", helper.getConnectionString());
        helper.setConnectionString("t2");
        assertEquals("t2", helper.getConnectionString());
    }

    public final void testGetConnection() throws Exception {
        SetupHelper.setProvider("de.cologneintelligence.fitgoodies.database.DriverMock");
        helper.setUser("username");
        helper.setPassword("pw1");
        helper.setConnectionString("jdbc://test/url");
        final Connection conn = helper.getConnection();
        assertNotNull(conn);
        assertEquals(DriverMock.getLastReturnedConnection(), conn);
    }
}
