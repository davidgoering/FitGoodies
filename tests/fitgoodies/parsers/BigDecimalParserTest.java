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


package fitgoodies.parsers;

import java.math.BigDecimal;

import fitgoodies.FitGoodiesTestCase;

/**
 * $Id: BigDecimalParserTest.java 185 2009-08-17 13:47:24Z jwierum $
 * @author jwierum
 */
public class BigDecimalParserTest extends FitGoodiesTestCase {
	public final void testParser() throws Exception {
		Parser<BigDecimal> p = new BigDecimalParser();

		assertEquals(new BigDecimal("42"), p.parse("42", null));
		assertEquals(new BigDecimal("21"), p.parse("21", null));
	}

	public final void testType() {
		assertNotNull(new BigDecimalParser().getType());
	}
}
