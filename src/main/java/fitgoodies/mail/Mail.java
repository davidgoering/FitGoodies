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


package fitgoodies.mail;

import javax.mail.MessagingException;

/**
 * Interface to access mail information.
 *
 * @author jwierum
 * @version $Id$
 */
public interface Mail {
	/**
	 * Returns all headers of the type <code>headerName</code>. If such a header
	 * does not exist, <code>null</code> should be returned.
	 *
	 * @param headerName header to read
	 * @return list of header values
	 * @throws MessagingException thrown by the underlying implementation
	 */
	String[] getHeader(String headerName) throws MessagingException;

	/**
	 * Tests whether a mail has an HTML body.
	 * @return true if, and only if, the mail contains an HTML body
	 * @throws MessagingException thrown by the underlying implementation
	 */
	boolean hasHTMLContent() throws MessagingException;

	/**
	 * Tests whether a mail has an plain text body.
	 * @return true if, and only if, the mail contains an plain text body
	 * @throws MessagingException thrown by the underlying implementation
	 */
	boolean hasPlainContent() throws MessagingException;

	/**
	 * Gets the HTML body.
	 * @return the HTML body of the mail.
	 */
	String getHTMLContent();

	/**
	 * Gets the plain text body.
	 * @return the plain text body of the mail.
	 */
	String getPlainContent();

	/**
	 * Marks the mail as deleted.
	 */
	void delete();
}