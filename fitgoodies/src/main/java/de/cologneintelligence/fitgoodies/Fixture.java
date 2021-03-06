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


package de.cologneintelligence.fitgoodies;

import de.cologneintelligence.fitgoodies.adapters.TypeAdapterHelper;
import de.cologneintelligence.fitgoodies.parsers.ParserHelper;
import de.cologneintelligence.fitgoodies.references.CrossReferenceHelper;
import de.cologneintelligence.fitgoodies.util.DependencyManager;
import de.cologneintelligence.fitgoodies.util.FixtureTools;
import fit.Parse;
import fit.TypeAdapter;

/**
 * In contrast to <code>fit.Fixture</code>, this <code>Fixture</code>
 * enables all fitgoodies features (for example custom type adapters,
 * custom parsers and cross references).
 *
 * @author jwierum
 * @version $Id$
 */
public class Fixture extends fit.Fixture {
	private String cellParameter;

	/**
	 * Replacement of <code>check</code> which resolves cross-references
	 * before calling the original check method of fit.
	 *
	 *  @param cell the cell to check
     *  @param a - TypeAdapter to use
     *
     *  @see fit.Fixture#check {@link fit.Fixture#check(Parse, TypeAdapter)}
     */
	@Override
	public void check(final Parse cell, final TypeAdapter a) {
	    final TypeAdapterHelper taHelper = DependencyManager.getOrCreate(TypeAdapterHelper.class);
		TypeAdapter ta = FixtureTools.rebindTypeAdapter(a, cellParameter, taHelper);
		final CrossReferenceHelper helper = DependencyManager.getOrCreate(CrossReferenceHelper.class);
		ta = FixtureTools.processCell(cell, ta, this, helper);
		if (ta != null) {
			super.check(cell, ta);
		}
	}

	/**
	 * Replacement of <code>parse</code> which uses the extended parse features of
	 * fitgoodies and uses fit's parse as a fallback.
	 *
	 *  @param text text to parse
     *  @param type type to transform text to
     *
     *  @return Object of type <code>type</code> which represents <code>text</code>
     *  @throws Exception if the value can't be parsed
     *
     *  @see fit.Fixture#parse(String, Class) {@link fit.Fixture#parse(String, Class)}
	 */
	@Override @SuppressWarnings("rawtypes")
	public Object parse(final String text, final Class type) throws Exception {
	    final ParserHelper helper = DependencyManager.getOrCreate(ParserHelper.class);
		final Object result = FixtureTools.parse(text, type, cellParameter, helper);

		if (result == null) {
			return super.parse(text, type);
		} else {
			return result;
		}
	}

	/**
	 * Sets the fixture parameters.
	 *
	 * Normally, these values are generated by reading the first
	 * line of the table. This method is primary useful for debugging.
	 * You won't need it otherwise.
	 *
	 * @param parameters parameters to store in <code>args</code>
	 */
	public final void setParams(final String[] parameters) {
		this.args = parameters;
	}

	/**
	 * Initializes the fixture arguments, call <code>setUp</code>,
	 * <code>fit.Fixture.doTable(Parse)</code> and <code>tearDown()</code>.
	 *
     * @param table the table to be processed
	 * @see fit.Fixture#doTable(Parse) {@link fit.Fixture#doTable(Parse)}
	 */
    @Override
    public void doTable(final Parse table) {
    	FixtureTools.copyParamsToFixture(args, this,
    	        DependencyManager.getOrCreate(CrossReferenceHelper.class),
    	        DependencyManager.getOrCreate(TypeAdapterHelper.class));

    	try {
    		setUp();

    		try {
	            super.doTable(table);
	        } catch (final Exception e) {
	            exception(table.parts.parts, e);
	        }

        	tearDown();
    	} catch (final Exception e) {
            exception(table.parts.parts, e);
        }
    }

    /**
     * Does nothing. Override it to initialize the fixture.
     * The method is called before doTables.
     * @throws Exception any kind of exception aborts the execution of this fixture
     */
    public void setUp() throws Exception {
    }

    /**
     * Does nothing. Override it to tear down the fixture.
     * The method is called after doTables.
     *
     * @throws Exception any kind of exception aborts the execution of this fixture
     */
    public void tearDown() throws Exception {
    }

    /**
     * Looks up a given parameter in the fixture's argument list.
     *
     * @param paramName the parameter name to look up
     * @return  the parameter value, if it could be found, <code>null</code> otherwise
     * @see #getParam(String, String) {@link #getParam(String, String)}
     * @see FixtureTools#getArg(String[], String, String)
     * 		{@link FixtureTools#getArg(String[], String, String)}
     */
	public final String getParam(final String paramName) {
		return getParam(paramName, null);
	}

	/**
	 * Looks up a given parameter in the fixture's argument list.
	 *
	 * If the value does not exist, the given default value is returned.
     * @param paramName paramName the parameter name to look up
     * @param defaultValue defaultValue the value to be returned if the parameter is missing
     * @return the parameter value, if it could be found, <code>defaultValue</code> otherwise
	 */
	public final String getParam(final String paramName, final String defaultValue) {
		return FixtureTools.getArg(args, paramName, defaultValue,
		        DependencyManager.getOrCreate(CrossReferenceHelper.class));
	}

	/**
	 * Sets the parameter of the selected cell.
	 * @param parameter the cell parameter to set
	 * @see #getCellParameter() getCellParameter()
	 */
	protected final void setCellParameter(final String parameter) {
		this.cellParameter = parameter;
	}

	/**
	 * Returns the parameter of the selected cell.
	 * @return the cell parameter
	 * @see #setCellParameter(String) setCellParameter(String)
	 */
	protected final String getCellParameter() {
		return cellParameter;
	}
}
