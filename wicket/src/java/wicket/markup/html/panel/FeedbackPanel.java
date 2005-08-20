/*
 * $Id$ $Revision:
 * 1.44 $ $Date$
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
package wicket.markup.html.panel;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import wicket.AttributeModifier;
import wicket.Component;
import wicket.feedback.FeedbackMessage;
import wicket.feedback.FeedbackMessagesModel;
import wicket.feedback.IFeedback;
import wicket.feedback.IFeedbackMessageFilter;
import wicket.markup.html.WebMarkupContainer;
import wicket.markup.html.basic.Label;
import wicket.markup.html.list.ListItem;
import wicket.markup.html.list.ListView;
import wicket.model.IModel;
import wicket.model.Model;

/**
 * A panel that displays {@link wicket.feedback.FeedbackMessage}s in a list
 * view. The maximum number of messages to show can be set with
 * setMaxMessages().
 * 
 * @see wicket.feedback.FeedbackMessage
 * @see wicket.feedback.FeedbackMessages
 * @author Jonathan Locke
 * @author Eelco Hillenius
 */
public class FeedbackPanel extends Panel implements IFeedback
{
	/** whether model messages should be HTML escaped. Default is true. */
	private boolean escapeMessages = true;

	/** Message view */
	private final MessageListView messageListView;

	/**
	 * List for messages.
	 */
	private final class MessageListView extends ListView
	{
		/**
		 * @see wicket.Component#Component(String)
		 */
		public MessageListView(final String id)
		{
			super(id);
			setModel(newFeedbackMessagesModel());
		}

		/**
		 * @see wicket.markup.html.list.ListView#populateItem(wicket.markup.html.list.ListItem)
		 */
		protected void populateItem(final ListItem listItem)
		{
			final FeedbackMessage message = (FeedbackMessage)listItem.getModelObject();
			final IModel replacementModel = new Model()
			{
				/**
				 * Returns feedbackPanel + the message level, eg
				 * 'feedbackPanelERROR'. This is used as the class of the li /
				 * span elements.
				 * 
				 * @see wicket.model.IModel#getObject(Component)
				 */
				public Object getObject(final Component component)
				{
					return getCSSClass(message);
				}
			};

			final Label label = new Label("message", message.getMessage());
			label.setEscapeModelStrings(getEscapeMessages());
			final AttributeModifier levelModifier = new AttributeModifier("class", replacementModel);
			label.add(levelModifier);
			listItem.add(levelModifier);
			listItem.add(label);
		}
	}

	/**
	 * @see wicket.Component#Component(String)
	 */
	public FeedbackPanel(final String id)
	{
		super(id);
		WebMarkupContainer messagesContainer = new WebMarkupContainer("feedbackul")
		{
			public boolean isVisible()
			{
				return anyMessage();
			}
		};
		add(messagesContainer);
		this.messageListView = new MessageListView("messages");
		messageListView.setVersioned(false);
		messagesContainer.add(messageListView);
	}

	/**
	 * Gets whether model messages should be HTML escaped. Default is true.
	 * 
	 * @return whether model messages should be HTML escaped
	 */
	public final boolean getEscapeMessages()
	{
		return escapeMessages;
	}

	/**
	 * @see wicket.Component#isVersioned()
	 */
	public boolean isVersioned()
	{
		return false; // makes no sense to version the feedback panel
	}

	/**
	 * Sets whether model messages should be HTML escaped. Default is true.
	 * 
	 * @param escapeMessages
	 *            whether model messages should be HTML escaped
	 */
	public final void setEscapeMessages(boolean escapeMessages)
	{
		this.escapeMessages = escapeMessages;
	}

	/**
	 * @param maxMessages
	 *            The maximum number of feedback messages that this feedback
	 *            panel should show at one time
	 */
	public final void setMaxMessages(int maxMessages)
	{
		this.messageListView.setViewSize(maxMessages);
	}

	/**
	 * Sets the comparator used for sorting the messages.
	 * 
	 * @param sortingComparator
	 *            comparator used for sorting the messages.
	 */
	public final void setSortingComparator(Comparator sortingComparator)
	{
		FeedbackMessagesModel feedbackMessagesModel = (FeedbackMessagesModel)messageListView
				.getModel();
		feedbackMessagesModel.setSortingComparator(sortingComparator);
	}

	/**
	 * @see wicket.feedback.IFeedback#updateFeedback()
	 */
	public void updateFeedback()
	{
		// Force model to load
		messageListView.getModelObject();
	}

	/**
	 * Search messages that this panel will render, and see if there is any
	 * message of level ERROR or up. This is a convenience method; same as
	 * calling 'anyMessage(FeedbackMessage.ERROR)'.
	 * 
	 * @return whether there is any message for this panel of level ERROR or up
	 */
	protected final boolean anyErrorMessage()
	{
		return anyMessage(FeedbackMessage.ERROR);
	}

	/**
	 * Search messages that this panel will render, and see if there is any
	 * message.
	 * 
	 * @return whether there is any message for this panel
	 */
	protected final boolean anyMessage()
	{
		return anyMessage(FeedbackMessage.UNDEFINED);
	}

	/**
	 * Search messages that this panel will render, and see if there is any
	 * message of the given level.
	 * 
	 * @param level
	 *            the level, see FeedbackMessage
	 * @return whether there is any message for this panel of the given level
	 */
	protected final boolean anyMessage(int level)
	{
		List msgs = getCurrentMessages();

		for (Iterator i = msgs.iterator(); i.hasNext();)
		{
			FeedbackMessage msg = (FeedbackMessage)i.next();
			if (msg.isLevel(level))
			{
				return true;
			}
		}

		return false;
	}

	/**
	 * Gets the css class for the given message.
	 * 
	 * @param message
	 *            the message
	 * @return the css class; by default, this returns feedbackPanel + the
	 *         message level, eg 'feedbackPanelERROR', but you can override this
	 *         method to provide your own
	 */
	protected String getCSSClass(final FeedbackMessage message)
	{
		return "feedbackPanel" + message.getLevelAsString();
	}

	/**
	 * Gets the currently collected messages for this panel.
	 * 
	 * @return the currently collected messages for this panel, possibly empty
	 */
	protected final List getCurrentMessages()
	{
		final List messages = (List)messageListView.getModelObject();
		return Collections.unmodifiableList(messages);
	}

	/**
	 * Gets a new instance of FeedbackMessagesModel to use.
	 * 
	 * @return instance of FeedbackMessagesModel to use
	 */
	protected FeedbackMessagesModel newFeedbackMessagesModel()
	{
		return new FeedbackMessagesModel(getFeedbackMessageFilter());
	}

	/**
	 * @return Let subclass specify some other filter
	 */
	protected IFeedbackMessageFilter getFeedbackMessageFilter()
	{
		return null;
	}
}
