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


package fitgoodies.mail.providers;
import java.util.Properties;

import javax.mail.Folder;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;

import fitgoodies.mail.JavaMailMail;
import fitgoodies.mail.Mail;

/**
 * Implementation of MessageProvider which uses the JavaMail libraries.
 *
 * @author jwierum
 * @version $Id: JavaMailMessageProvider.java 185 2009-08-17 13:47:24Z jwierum $
 */
public class JavaMailMessageProvider implements MessageProvider {
	private final Session session;
	private final String folderName;
	private Store store;
	private Folder folder;

	/**
	 * Constructs a JavaMailMessageProvider object.

	 * @param props properties which are handed to the JavaMail session.
	 * 		This parameter <code>Properties</code> object must also provide
	 * 		&quote;mail.username&quote;, &quote;mail.password&quote; and
	 * 		&quote;mail.inbox&quote;.
	 */
	public JavaMailMessageProvider(final Properties props) {

		session = Session.getInstance(props,
				new StaticAuthenticator(
						props.getProperty("mail.username"),
						props.getProperty("mail.password")));
		this.folderName = props.getProperty("mail.inbox");
	}

	public void connect() throws MessagingException {
		disconnect();
		store = session.getStore();
		store.connect();

		folder = store.getFolder(folderName);
		folder.open(Folder.READ_WRITE);
	}

	public void disconnect() throws MessagingException {
		if (folder != null) {
			folder.close(true);
		}
		if (store != null) {
			store.close();
		}
	}

	public Mail getLatestMessage()
			throws MessagingException {

		int messages = folder.getMessageCount();
		if (messages > 0) {
			return new JavaMailMail(folder.getMessage(messages));
		} else {
			return null;
		}
	}
}
