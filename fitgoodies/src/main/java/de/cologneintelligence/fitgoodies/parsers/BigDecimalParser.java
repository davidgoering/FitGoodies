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

import java.math.BigDecimal;

/**
 * Parser which is able to convert a string into a <code>BigDecimal</code>.
 *
 * @author jwierum
 * @version $Id$
 */
public class BigDecimalParser implements Parser<BigDecimal> {
	/**
	 * Parses a string and converts it into a <code>BigDecimal</code> object.
	 * @param s <code>String</code> which will be converted
	 * @param parameter ignored
	 * @return <code>BigDecimal</code> object which is represented by <code>s</code>
	 */
	@Override
	public final BigDecimal parse(final String s, final String parameter) {
		return new BigDecimal(s);
	}

	/**
	 * Returns the destination class which is managed by this parser.
	 * @return BigDecimal.class
	 */
	@Override
	public final Class<BigDecimal> getType() {
		return BigDecimal.class;
	}

}
