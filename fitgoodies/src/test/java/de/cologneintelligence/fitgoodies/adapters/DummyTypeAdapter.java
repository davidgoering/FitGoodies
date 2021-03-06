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


package de.cologneintelligence.fitgoodies.adapters;

import java.math.BigInteger;

import de.cologneintelligence.fitgoodies.adapters.AbstractTypeAdapter;

import fit.TypeAdapter;

/**
 *
 * @author jwierum
 */
public class DummyTypeAdapter extends AbstractTypeAdapter<BigInteger> {
	public DummyTypeAdapter(final TypeAdapter ta, final String x) {
		super(ta, x);
	}

	@Override
	public final String toString(final Object o) { return null; }

	@Override
	public final Class<BigInteger> getType() {
		return BigInteger.class;
	}

	@Override
	public final Object parse(final String x) {
		if (getParameter() != null) {
			return new BigInteger("23");
		} else {
			return new BigInteger("42");
		}
	}
}
