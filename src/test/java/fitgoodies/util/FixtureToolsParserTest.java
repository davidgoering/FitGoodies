/*
 * Copyright (c) 2009  Cologne Intelligence GmbH
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


package fitgoodies.util;

import java.text.ParseException;

import fit.Parse;
import fit.TypeAdapter;
import fitgoodies.ColumnFixture;
import fitgoodies.FitGoodiesTestCase;
import fitgoodies.adapters.CachingTypeAdapter;
import fitgoodies.util.FixtureTools;

/**
 * $Id$
 * @author jwierum
 */
public final class FixtureToolsParserTest extends FitGoodiesTestCase {
	public static class DummyFixture extends ColumnFixture {
		private int calls = 0;
		public String value = "xyz";

		public final int getValue() {
			++calls;
			return 42;
		}
	}

	private DummyFixture dummy;
	private TypeAdapter taMethod;
	private TypeAdapter taField;

	@Override
	public void setUp() throws Exception {
		super.setUp();
		dummy = new DummyFixture();
		taMethod = TypeAdapter.on(dummy,
				DummyFixture.class.getMethod("getValue", new Class<?>[]{}));
		taField = TypeAdapter.on(dummy,
				DummyFixture.class.getField("value"));
	}

	public void testCachedAdapter() throws ParseException {
		Parse cell = new Parse("<td>x</td>", new String[]{"td"});
		TypeAdapter ta = FixtureTools.processCell(cell, taMethod, dummy);
		assertEquals(ta.getClass(), CachingTypeAdapter.class);

		cell = new Parse("<td>another value</td>", new String[]{"td"});
		ta = FixtureTools.processCell(cell, taMethod, dummy);
		assertEquals(ta.getClass(), CachingTypeAdapter.class);
	}

	public void testProcessWithPositiveShortcuts() throws ParseException {
		Parse cell = new Parse("<td>${nonEmpty()}</td>", new String[]{"td"});
		TypeAdapter ta = FixtureTools.processCell(cell, taField, dummy);

		assertNull(ta);
		assertContains("empty",  cell.text());

		cell = new Parse("<td>${nonEmpty()}</td>", new String[]{"td"});
		dummy.value = null;
		ta = FixtureTools.processCell(cell, taField, dummy);

		assertNull(ta);
		assertContains("(null)", cell.text());
		assertContains("value must not be empty", cell.text());
	}

	public void testProcessWithNegativeShortcuts()  throws ParseException {
		Parse cell = new Parse("<td>${empty()}</td>", new String[]{"td"});
		TypeAdapter ta = FixtureTools.processCell(cell, taField, dummy);

		assertNull(ta);
		assertContains("value must be empty", cell.text());

		cell = new Parse("<td>${empty()}</td>", new String[]{"td"});
		dummy.value = null;
		ta = FixtureTools.processCell(cell, taField, dummy);
		assertNull(ta);
		assertTrue(cell.text().startsWith("(null)"));
		assertContains("value is empty", cell.text());


		cell = new Parse("<td>${empty()}</td>", new String[]{"td"});
		dummy.value = "";
		ta = FixtureTools.processCell(cell, taField, dummy);
		assertNull(ta);
		assertContains("value is empty", cell.text());
	}
}