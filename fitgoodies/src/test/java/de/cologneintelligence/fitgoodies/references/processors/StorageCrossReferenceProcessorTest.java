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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.cologneintelligence.fitgoodies.FitGoodiesTestCase;
import de.cologneintelligence.fitgoodies.references.CrossReference;
import de.cologneintelligence.fitgoodies.references.CrossReferenceProcessorShortcutException;
import de.cologneintelligence.fitgoodies.references.processors.AbstractCrossReferenceProcessor;
import de.cologneintelligence.fitgoodies.references.processors.StorageCrossReferenceProcessor;


/**
 *
 * @author jwierum
 */

public class StorageCrossReferenceProcessorTest extends FitGoodiesTestCase {
    private AbstractCrossReferenceProcessor processor;

    @Override
    public final void setUp() throws Exception {
        super.setUp();
        processor = new StorageCrossReferenceProcessor();
    }

    public final void testPattern() {
        String pattern = processor.getPattern();
        pattern = "^\\$\\{" + pattern + "\\}$";

        Pattern regex = Pattern.compile(pattern);
        Matcher m;

        m = regex.matcher("${ns.get(one)}");
        assertTrue(m.find());

        m = regex.matcher("${ns.get(two)}");
        assertTrue(m.find());

        m = regex.matcher("${x.get(three)}");
        assertTrue(m.find());

        m = regex.matcher("${xy.put(four)}");
        assertTrue(m.find());

        m = regex.matcher("${six.containsValue(five)}");
        assertTrue(m.find());

        m = regex.matcher("${six.containsValue(five, /a\\/(b)\\/c/)}");
        assertTrue(m.find());
    }

    public final void testNegativePattern() {
        String pattern = processor.getPattern();
        pattern = "^\\$\\{" + pattern + "\\}$";

        Pattern regex = Pattern.compile(pattern);
        Matcher m;

        m = regex.matcher("${six.containsValue()}");
        assertFalse(m.find());

        m = regex.matcher("${.put(x)}");
        assertFalse(m.find());

        m = regex.matcher("${put(x)}");
        assertFalse(m.find());

        m = regex.matcher("${x.putput(y)}");
        assertFalse(m.find());

        m = regex.matcher("${x.put(y, /a/b/)}");
        assertFalse(m.find());
    }

    private void checkCr(final CrossReference cr, final String cmd,
            final String namespace, final String parameter) {
        assertNotNull(cr);
        assertEquals(cmd, cr.getCommand());
        assertEquals(namespace, cr.getNamespace());
        assertEquals(parameter, cr.getParameter());
        assertSame(processor, cr.getProcessor());
    }

    public final void testExtraction() {
        CrossReference cr;

        cr = processor.extractCrossReference("ns1.put(x)");
        checkCr(cr, "put", "ns1", "x");

        cr = processor.extractCrossReference("n.put(x3)");
        checkCr(cr, "put", "n", "x3");

        cr = processor.extractCrossReference("ns3.get(param)");
        checkCr(cr, "get", "ns3", "param");

        cr = processor.extractCrossReference("n.containsValue(param)");
        checkCr(cr, "containsValue", "n", "param");

        assertNull(processor.extractCrossReference("ns3.err(param)"));
    }

    public final void checkProcessing(
            final String cmd, final String ns, final String param,
            final Object object, final String expected)
                    throws CrossReferenceProcessorShortcutException {

        String actual = processor.processMatch(
                new CrossReference(cmd, ns, param, null), object);
        assertEquals(expected, actual);
    }

    public final void testProcessing()
            throws CrossReferenceProcessorShortcutException {

        checkProcessing("put", "one", "x", 42, "42");
        checkProcessing("get", "one", "x", 21, "42");
        checkProcessing("put", "one", "x", new StringBuilder("two"), "two");
        checkProcessing("get", "one", "x", null, "two");


        checkProcessing("get", "two", "n", null,
                "two.n: cross reference could not be resolved!");

        checkProcessing("containsValue", "two", "z", "good",
                "two.z: no value found!");
        checkProcessing("containsValue", "one", "x", "good",
                "good");
    }

    public final void testRegexExtraction()
            throws CrossReferenceProcessorShortcutException {

        checkProcessing("put", "retest", "val1, /my\\/([^\\/]+)\\/test/",
                "my/simple/test", "my/simple/test");
        checkProcessing("get", "retest", "val1",
                "x", "simple");

        checkProcessing("put", "retest", "val2, /a(b)c/",
                "abc", "abc");
        checkProcessing("get", "retest", "val2",
                "x", "b");

        checkProcessing("put", "retest", "val3, /a/c/",
                "abc", "/a/c/: illegal regex");
        checkProcessing("get", "retest", "val3", null,
                "retest.val3: cross reference could not be resolved!");
    }
}
