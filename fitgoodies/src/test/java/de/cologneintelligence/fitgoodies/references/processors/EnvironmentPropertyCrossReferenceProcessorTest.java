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

package de.cologneintelligence.fitgoodies.references.processors;

import org.jmock.Expectations;
import org.jmock.Mockery;

import de.cologneintelligence.fitgoodies.references.CrossReference;
import de.cologneintelligence.fitgoodies.references.processors.EnvironmentPropertyCrossReferenceProcessor;
import de.cologneintelligence.fitgoodies.references.processors.PropertyProvider;

import junit.framework.TestCase;

public class EnvironmentPropertyCrossReferenceProcessorTest extends TestCase {

	public void testGetJavaHome() throws Exception {
		Mockery mockery = new Mockery();
		final PropertyProvider propertyProvider = mockery.mock(PropertyProvider.class);
		mockery.checking(new Expectations(){{
			oneOf(propertyProvider).getProperty("key"); will(returnValue("testProperty"));
		}});
		
		EnvironmentPropertyCrossReferenceProcessor processor = new EnvironmentPropertyCrossReferenceProcessor(propertyProvider);
		
		CrossReference cr = new CrossReference("getProperty", "System", "key", processor);
		assertEquals("testProperty", processor.processMatch(cr  , null));
		
	}
}
