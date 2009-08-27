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

import fitgoodies.file.readers.DelimiterRecordReader;

/**
 * {@link AbstractFileRecordReaderFixture} implementation which processes character
 * delimited files using a {@link fitgoodies.file.readers.DelimiterRecordReader}.
 * The fixture has one more parameters: delimiter. The delimiter must be set.
 *
 * @author jwierum
 * @version $Id: DelimiterFileRecordFixture.java 185 2009-08-17 13:47:24Z jwierum $
 */
public class DelimiterFileRecordFixture extends AbstractFileRecordReaderFixture {
	/** for internal use - used to resolve cross references. */
	public String delimiter;

	@Override
	public void setUp() throws Exception {
		super.setUp();

		setRecordReader(new DelimiterRecordReader(
				getFile().openBufferedReader(super.getEncoding()), delimiter));
	}
}
