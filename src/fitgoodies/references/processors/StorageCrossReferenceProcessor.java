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


package fitgoodies.references.processors;
import fitgoodies.references.CrossReference;

/**
 * This processor allows to store and receive string values. To keep things clean,
 * the processor uses namespaces.<br /><br />
 *
 * Valid examples and return values:<br /><ul>
 * <li>${testnamespace.put(var1)} with object &quot;x&quot;: x</li>
 * <li>${testnamespace.put(var2)} with object &quot;y&quot;: y</li>
 * <li>${ns2.put(var2)} with object &quot;x&quot;: x</li>
 * <li>${ns2.get(var2)} with object &quot;y&quot;: x</li>
 * <li>${ns2.containsValue(var1)} with object &quot;y&quot;: ns2.var1: no value found!</li>
 * <li>${testnamespace.containsValue(var1)} with object &quot;y&quot;: y</li>
 * </ul>
 *
 * @author jwierum
 * @version $Id: StorageCrossReferenceProcessor.java 185 2009-08-17 13:47:24Z jwierum $
 */

public class StorageCrossReferenceProcessor extends AbstractCrossReferenceProcessor {
	private static final String PATTERN =
		"([^.()]+)\\.(get|put|containsValue)\\(([^)]+)\\)";

	private final NamespaceHashMap<String> variablesMap =
		new NamespaceHashMap<String>();

	/**
	 * Default constructor.
	 */
	public StorageCrossReferenceProcessor() {
		super(PATTERN);
	}

	/**
	 * Processes a match. The namespace represents the namespace, the parameter
	 * represents the variable's name.<br /><br />
	 *
	 * The return value depends on what command is used. If it is &quot;get&quot;,
	 * the loaded value or an error is returned. If it is &quot;put&quot;,
	 * <code>object</code> is returned, and if it is &quot;containsValue&quot;,
	 * either the object or an error is returned.
	 *
	 * @param cr the extracted match
	 * @param object the object to save
	 * @return <code>object</code>, an error message or the loaded value, depending
	 * 		on <code>cr.getcommand()</code> (see method description above).
	 */
	@Override
	public final String processMatch(final CrossReference cr, final Object object) {
		String result = null;
		if (cr.getCommand().equals("get")) {
			result = getValue(cr);
		} else if (cr.getCommand().equals("put")) {
			result = putValue(cr, object);
		} else if (cr.getCommand().equals("containsValue")) {
			result = variablesMap.get(cr.getNamespace(), cr.getParameter());

			if (result == null) {
				result = cr.getNamespace() + "." + cr.getParameter()
					+ ": no value found!";
			} else {
				result = object.toString();
			}
		}

		return result;
	}

    private String putValue(
    		final CrossReference cr, final Object object) {
        String result;
        variablesMap.put(cr.getNamespace(), cr.getParameter(), object.toString());
        result = object.toString();
        return result;
    }

    private String getValue(final CrossReference cr) {
        String result;
        result = variablesMap.get(cr.getNamespace(), cr.getParameter());

        if (result == null) {
        	result = cr.getNamespace() + "." + cr.getParameter()
        		+ ": cross reference could not be resolved!";
        }
        return result;
    }

	/**
	 * A user friendly description.
	 * @return a description.
	 */
	@Override
	public final String info() {
		return "provides get(), set() and containsValue()";
	}
}
