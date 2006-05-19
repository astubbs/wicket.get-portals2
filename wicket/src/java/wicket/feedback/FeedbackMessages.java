/*
 * $Id$
 * $Revision$ $Date$
 * 
 * ==============================================================================
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package wicket.feedback;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import wicket.Component;
import wicket.util.string.StringList;

/**
 * Holds list of feedback messages. The list can be added to, cleared, queried
 * and filtered.
 * 
 * @author Eelco Hillenius
 * @author Jonathan Locke
 */
public final class FeedbackMessages implements Serializable
{
	/** Log. */
	private static Log log = LogFactory.getLog(FeedbackMessages.class);

	private static final long serialVersionUID = 1L;

	/**
	 * Holds a list of {@link wicket.feedback.FeedbackMessage}s.
	 */
	private ArrayList<FeedbackMessage> messages = null;

	/**
	 * Package local constructor; clients are not allowed to create instances as
	 * this class is managed by the framework.
	 * 
	 * @param sessionStored
	 *            whether or not this object is stored in session
	 */
	public FeedbackMessages()
	{
	}

	/**
	 * Clears any existing messages
	 */
	public final void clear()
	{
		if (messages != null)
		{
			messages.clear();
		}
	}

	/**
	 * Gets the number of messages
	 * 
	 * @return the number of messages
	 */
	public final int size()
	{
		if (messages == null)
		{
			return 0;
		}
		else
		{
			return messages.size();
		}
	}

	/**
	 * Removes messages that have been rendered
	 */
	public final void clearRendered()
	{
		Iterator<FeedbackMessage> msgs = iterator();
		while (msgs.hasNext())
		{
			final FeedbackMessage msg = msgs.next();
			if (msg.isRendered())
			{
				msgs.remove();
			}
		}
	}

	/**
	 * Adds a new ui message with level DEBUG to the current messages.
	 * 
	 * @param reporter
	 *            the reporting component
	 * @param message
	 *            the actual message
	 */
	public final void debug(Component reporter, String message)
	{
		add(new FeedbackMessage(reporter, message, FeedbackMessage.DEBUG));
	}

	/**
	 * Adds a new ui message with level ERROR to the current messages.
	 * 
	 * @param reporter
	 *            the reporting component
	 * @param message
	 *            the actual message
	 */
	public final void error(Component reporter, String message)
	{
		add(new FeedbackMessage(reporter, message, FeedbackMessage.ERROR));
	}

	/**
	 * Adds a new ui message with level FATAL to the current messages.
	 * 
	 * @param reporter
	 *            the reporting component
	 * @param message
	 *            the actual message
	 */
	public final void fatal(Component reporter, String message)
	{
		add(new FeedbackMessage(reporter, message, FeedbackMessage.FATAL));
	}

	/**
	 * Convenience method that looks up whether the given component registered a
	 * message with this list with the level ERROR.
	 * 
	 * @param component
	 *            the component to look up whether it registered a message
	 * @return whether the given component registered a message with this list
	 *         with level ERROR
	 */
	public final boolean hasErrorMessageFor(Component component)
	{
		return hasMessageFor(component, FeedbackMessage.ERROR);
	}

	/**
	 * @param filter
	 *            Filter for selecting messages
	 * @return True if one or more messages matches the filter
	 */
	public final boolean hasMessage(final IFeedbackMessageFilter filter)
	{
		return messages(filter).size() != 0;
	}

	/**
	 * Looks up whether the given component registered a message with this list.
	 * 
	 * @param component
	 *            the component to look up whether it registered a message
	 * @return whether the given component registered a message with this list
	 */
	public final boolean hasMessageFor(Component component)
	{
		return messageForComponent(component) != null;
	}

	/**
	 * Looks up whether the given component registered a message with this list
	 * with the given level.
	 * 
	 * @param component
	 *            The component to look up whether it registered a message
	 * @param level
	 *            The level of the message
	 * @return Whether the given component registered a message with this list
	 *         with the given level
	 */
	public final boolean hasMessageFor(Component component, int level)
	{
		final FeedbackMessage message = messageForComponent(component);
		return message != null && message.isLevel(level);
	}

	/**
	 * Adds a new ui message with level INFO to the current messages.
	 * 
	 * @param reporter
	 *            The reporting component
	 * @param message
	 *            The actual message
	 */
	public final void info(Component reporter, String message)
	{
		add(new FeedbackMessage(reporter, message, FeedbackMessage.INFO));
	}

	/**
	 * Gets whether there are no messages.
	 * 
	 * @return True when there are no messages
	 */
	public final boolean isEmpty()
	{
		return messages == null || messages.isEmpty();
	}

	/**
	 * Looks up a message for the given component.
	 * 
	 * @param component
	 *            the component to look up the message for
	 * @return the message that is found for the given component (first match)
	 *         or null if none was found
	 */
	public final FeedbackMessage messageForComponent(final Component component)
	{
		if (messages != null)
		{
			for (Iterator<FeedbackMessage> iterator = messages.iterator(); iterator.hasNext();)
			{
				FeedbackMessage message = iterator.next();
				if (message.getReporter() == component)
				{
					return message;
				}
			}
		}
		return null;
	}

	/**
	 * Gets a list of messages from the page using a filter.
	 * 
	 * @param filter
	 *            Filter for selecting messages
	 * @return The messages or an empty list if no messages are found
	 */
	public final List<FeedbackMessage> messages(final IFeedbackMessageFilter filter)
	{
		if (messages == null)
		{
			return Collections.emptyList();
		}
		else
		{
			final List<FeedbackMessage> list = new ArrayList<FeedbackMessage>();
			for (final Iterator<FeedbackMessage> iterator = messages.iterator(); iterator.hasNext();)
			{
				final FeedbackMessage message = iterator.next();
				if (filter == null || filter.accept(message))
				{
					list.add(message);
				}
			}
			return list;
		}
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString()
	{
		return "[feedbackMessages = " + StringList.valueOf(messages) + "]";
	}

	/**
	 * Adds a new ui message with level WARNING to the current messages.
	 * 
	 * @param reporter
	 *            the reporting component
	 * @param message
	 *            the actual message
	 */
	public final void warn(Component reporter, String message)
	{
		add(new FeedbackMessage(reporter, message, FeedbackMessage.WARNING));
	}

	/**
	 * Adds a message
	 * @param reporter
	 * @param message
	 * @param level
	 */
	public final void add(Component reporter, String message, int level) {
		add(new FeedbackMessage(reporter, message, level));
	}
	
	/**
	 * Adds a message.
	 * 
	 * @param message
	 *            the message
	 */
	final void add(FeedbackMessage message)
	{
		if (log.isDebugEnabled())
		{
			log.debug("Adding feedback message " + message);
		}
		if (messages == null)
		{
			messages = new ArrayList<FeedbackMessage>();
		}
		messages.add(message);
	}

	/**
	 * Gets an iterator over stored messages
	 * 
	 * @return iterator over stored messages
	 */
	@SuppressWarnings("unchecked")
	public final Iterator<FeedbackMessage> iterator()
	{
		if (messages == null)
		{
			return ((List<FeedbackMessage>)Collections.EMPTY_LIST).iterator();
		}
		else
		{
			return messages.iterator();
		}
	}

	/**
	 * Frees any unnecessary internal storage
	 */
	public final void trimToSize()
	{
		if (messages!=null) {
			messages.trimToSize();
		}
	}
}