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


package fitgoodies.file;

import java.text.ParseException;

import fit.Parse;
import fitgoodies.FitGoodiesTestCase;
import fitgoodies.references.CrossReferenceHelper;

/**
 * $Id: FixedLengthFileRecordFixtureTest.java 186 2009-08-17 14:12:49Z jwierum $
 * @author jwierum
 */
public class FixedLengthFileRecordFixtureTest extends FitGoodiesTestCase {
	public final void testExtractWidth() throws ParseException {
		FixedLengthFileRecordFixture fixture = new FixedLengthFileRecordFixture();

		Parse row = new Parse("<tr><td>1</td><td>7</td><td>4</td></tr>",
				new String[]{"tr", "td"});

		int[] actual = fixture.extractWidth(row);

		assertEquals(3, actual.length);
		assertEquals(1, actual[0]);
		assertEquals(7, actual[1]);
		assertEquals(4, actual[2]);

		row = new Parse("<tr><td>3</td><td>1</td><td>9</td><td>0</td></tr>",
				new String[]{"tr", "td"});

		actual = fixture.extractWidth(row);

		assertEquals(4, actual.length);
		assertEquals(3, actual[0]);
		assertEquals(1, actual[1]);
		assertEquals(9, actual[2]);
		assertEquals(0, actual[3]);
	}

	public final void testExtractWidthWithCrossRefs() throws Exception {
		FixedLengthFileRecordFixture fixture = new FixedLengthFileRecordFixture();

		Parse row = new Parse("<tr><td>1</td><td>${width.get(col2)}</td></tr>",
				new String[]{"tr", "td"});

		CrossReferenceHelper.instance().parseBody("${width.put(col2)}", 23);
		int[] actual = fixture.extractWidth(row);

		assertEquals(2, actual.length);
		assertEquals(1, actual[0]);
		assertEquals(23, actual[1]);
	}

	public final void testErrors() throws ParseException {
		FixedLengthFileRecordFixture fixture = new FixedLengthFileRecordFixture();

		Parse row = new Parse("<tr><td>1</td><td>error</td><td>4</td></tr>",
				new String[]{"tr", "td"});

		int[] actual = fixture.extractWidth(row);

		assertNull(actual);
		assertEquals(1, fixture.counts.exceptions);
		assertContains("NumberFormatException", row.parts.more.text());
	}
}
