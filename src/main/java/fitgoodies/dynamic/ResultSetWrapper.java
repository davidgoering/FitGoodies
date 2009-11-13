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


package fitgoodies.dynamic;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import fitgoodies.ScientificDouble;

/**
 * Maps a ResultSet to an Array of dynamic generated objects.
 * These objects can be read from fit.
 *
 * @author jwierum
 * @version $Id$
 */
public class ResultSetWrapper {
	private final List<String> names = new ArrayList<String>();
	private final List<Class<?>> types = new ArrayList<Class<?>>();
	private final List<Object[]> rows = new ArrayList<Object[]>();
	private final ResultSet resultSet;
	private final int columnCount;
	private Class<?> clazz;

	private int analyzeRow() throws SQLException {
		Object[] row = new Object[columnCount];
		int result = 0;

		for (int col = 0; col < columnCount; ++col) {
			row[col] = resultSet.getObject(col + 1);

			if (types.get(col) == null && row[col] != null) {
				Class<?> c = row[col].getClass();
				if (c.equals(Float.class) || c.equals(Float.TYPE)
						|| c.equals(Double.class) || c.equals(Double.TYPE)) {
					c = ScientificDouble.class;
				}
				types.set(col, c);
				++result;
			}
		}
		rows.add(row);

		return result;
	}

	/**
	 * Generates a new wrapper which maps <code>rs</code>.
	 * @param source <code>ResultSet</code> to wrap
	 * @throws SQLException generated by <code>rs</code> while reading it.
	 */
	public ResultSetWrapper(final ResultSet source) throws SQLException {
		int found = 0;

		ResultSetMetaData meta = source.getMetaData();
		columnCount = meta.getColumnCount();
		for (int i = 1; i <= columnCount; ++i) {
			types.add(null);
		}

		this.resultSet = source;
		while (found < columnCount && source.next()) {
			found += analyzeRow();
		}

		makeClass(meta);
	}

	private void makeClass(final ResultSetMetaData meta) throws SQLException {
		DynamicObjectFactory factory = new DynamicObjectFactory();
		for (int i = 1; i <= columnCount; ++i) {
			String columnName = meta.getColumnName(i);
			names.add(columnName);

			if (types.get(i - 1) == null) {
				types.set(i - 1, Object.class);
			}

			try {
				factory.add(
						types.get(i - 1),
						columnName);
			} catch (ClassNotFoundException e) {
				throw new RuntimeException(e);
			}
		}
		clazz = factory.compile();
	}

	/**
	 * Creates a new, empty object which has the same fields as the saved
	 * <code>ResultSet</code>.
	 * @return dynamic object for one record set
	 */
	public final Object createContainerObject() {
		try {
			return clazz.newInstance();
		} catch (InstantiationException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Generates an array which represents the whole <code>ResultSet</code>.
	 * Each element equates to one row in the <code>ResultSet</code>.
	 * The elements have public fields with the same name as the <code>ResultSet</code> columns.
	 * The {@link fitgoodies.ScientificDouble} class is automatically used.
	 * They can be read them using reflections.
	 * @return array of dynamic objects
	 * @throws SQLException thrown, if the <code>ResultSet</code> throws an Exception
	 */
	public final Object[] getRows() throws SQLException {
		List<Object> result = new ArrayList<Object>();

		while (rows.size() > 0) {
			Object[] copy = rows.remove(0);
			Object o = createContainerObject();

			for (int i = 0; i < columnCount; ++i) {
				setObjectValue(o, names.get(i), copy[i]);
			}

			result.add(o);
		}

		while (resultSet.next()) {
			Object o = createContainerObject();
			for (int col = 0; col < columnCount; ++col) {
				setObjectValue(o, names.get(col), resultSet.getObject(col + 1));
			}
			result.add(o);
		}

		return result.toArray();
	}

	private void setObjectValue(
			final Object o, final String valueName, final Object value) {
		try {
			if (mustConvertToScientificDouble(o, valueName, value)) {
				ScientificDouble sdbl = new ScientificDouble((Double) value);
				o.getClass().getField(valueName).set(o, sdbl);
			} else {
				o.getClass().getField(valueName).set(o, value);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private boolean mustConvertToScientificDouble(final Object o,
			final String valueName, final Object value) throws NoSuchFieldException {
		Class<?> destClass = o.getClass().getField(valueName).getType();
		return destClass.equals(ScientificDouble.class)
			&& !value.getClass().equals(ScientificDouble.class);
	}

	/**
	 * Gets the class of the dynamic generated object.
	 * @return Class of the dynamic generated object.
	 */
	public final Class<?> getClazz() {
		return clazz;
	}
}