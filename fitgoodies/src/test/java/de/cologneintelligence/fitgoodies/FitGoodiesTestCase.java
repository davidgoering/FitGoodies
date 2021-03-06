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
import java.util.Comparator;
import java.util.Iterator;

import org.jmock.integration.junit3.MockObjectTestCase;

import de.cologneintelligence.fitgoodies.util.DependencyManager;
import fit.Counts;
import fit.Parse;

/**
 *
 * @author jwierum
 */
public abstract class FitGoodiesTestCase extends MockObjectTestCase {
    protected static Counts mkCounts(final int r, final int w, final int i,
            final int e) {
        final Counts c = new Counts();
        c.right = r; c.wrong = w; c.ignores = i; c.exceptions = e;
        return c;
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        DependencyManager.clear();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        de.cologneintelligence.fitgoodies.database.DriverMock.cleanup();
    }

    public final void assertStringIterator(final Iterator<?> it,
            final String[] expected) {
        int i = 0;

        while (it.hasNext()) {
            assertEquals(it.next().toString(), expected[i]);
            i++;
        }
        assertEquals(expected.length, i);
    }

    public final <T> void assertCompares(final T o1, final T o2,
            final Comparator<T> comp, final int expectedSign) {
        assertTrue("Unexpected result when comparing: "
                + o1.toString() + " <> " + o2.toString(),
                Math.signum(comp.compare(o1, o2)) == Math.signum(expectedSign));

        assertTrue("Unexpected result when comparing: "
                + o2.toString() + " <> " + o1.toString(),
                Math.signum(comp.compare(o2, o1)) == Math.signum(-expectedSign));
    }

    public final void assertContains(final Object[] array, final Object element) {
        for (final Object a : array) {
            if (element.equals(a)) {
                return;
            }
        }
        fail("array does not contain " + element.toString());
    }

    public final void assertArrayElements(
            final Object[] expected, final Object[] actual) {
        assertEquals(expected.length, actual.length);

        for (final Object e : expected) {
            assertContains(actual, e);
        }
    }

    public final void assertArray(
            final Object[] expected, final Object[] actual) {
        assertEquals(expected.length, actual.length);

        for (int i = 0; i < expected.length; ++i) {
            assertEquals(expected[i], actual[i]);
        }
    }

    public final void assertNotContains(final Object[] array, final Object element) {
        for (final Object a : array) {
            if (element.equals(a)) {
                fail("array contains " + element.toString());
            }
        }
    }

    protected final void assertNotEquals(final Object expected, final Object actual) {
        assertFalse("expected: anything but <" + expected + "> but was: <" + actual + ">",
                expected == actual);
    }

    public final void assertContains(final String needle, final String haystack) {
        assertNotNull("String was null", haystack);
        assertTrue("\"" + haystack + "\" did not contain \"" + needle + "\"",
                haystack.contains(needle));
    }

    public static void assertNull(final Object o) {
        if (o != null) {
            fail("Should be null, but is: " + o.toString());
        }
    }

    protected static void assertCounts(final Counts counts, final int right, final int wrong, final int ignores, final int exceptions) {
        assertCounts(counts, null, right, wrong, ignores, exceptions);
    }

    protected static void assertCounts(final Counts counts, final Parse table, final int right, final int wrong, final int ignores, final int exceptions) {
        if (exceptions != counts.exceptions) {
            final String message = "unexpected numer of exceptions count: expected: " + exceptions + ", got: " + counts.exceptions;
            if (table != null) {
                fail(message + "\nFirst exception is: " + findException(table));
            } else {
                fail(message);
            }
        }

        assertEquals("unexpected numer of right count: expected: " + right + ", got: " + counts.right, right, counts.right);
        assertEquals("unexpected numer of wrong count: expected: " + wrong + ", got: " + counts.wrong, wrong, counts.wrong);
        assertEquals("unexpected numer of ignores count: expected: " + ignores + ", got: " + counts.ignores, ignores, counts.ignores);
    }

    private static String findException(Parse table) {
        Parse row = table.parts;
        while (row != null) {
            Parse cell = row.parts;
            while (cell != null) {

                if (cell.tag.contains("bgcolor=\"" + fit.Fixture.yellow + "\"")) {
                    final String body = cell.body;
                    final String trace = body.replaceFirst("^.*<pre>", "").replaceFirst("</pre>.*$", "");
                    return Parse.unescape(trace);
                }

                cell = cell.more;
            }
            row = row.more;
        }
        throw new IllegalArgumentException("Table does not contain a stacktrace!");
    }
}
