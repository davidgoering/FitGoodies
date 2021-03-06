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


package de.cologneintelligence.fitgoodies.parsers;
import de.cologneintelligence.fitgoodies.FitGoodiesTestCase;
import de.cologneintelligence.fitgoodies.parsers.ObjectParser;
import de.cologneintelligence.fitgoodies.parsers.Parser;

/**
 *
 * @author jwierum
 *
 */
public class ObjectParserTest extends FitGoodiesTestCase {
	public final void testGetType() {
		Parser<Object> p = new ObjectParser();
		assertEquals(Object.class, p.getType());
	}

	public final void testParse() throws Exception {
		Parser<Object> p = new ObjectParser();
		assertEquals("test", p.parse("test", null));
		assertEquals("string", p.parse("string", null));
	}
}
