/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package wicket.feedback;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import wicket.Component;
import wicket.model.AbstractDetachableModel;
import wicket.model.IModel;

/**
 * Model for extracting feedback messages.
 * 
 * @author Eelco Hillenius
 */
public class FeedbackMessagesModel extends AbstractDetachableModel
{
	private static final long serialVersionUID = 1L;

	/** Message filter */
	private IFeedbackMessageFilter filter;

	/** Lazy loaded, temporary list. */
	private transient List messages;

	/** Comparator used for sorting the messages. */
	private Comparator sortingComparator;

	/**
	 * Constructor. Creates a model for all feedback messages on the page.
	 */
	public FeedbackMessagesModel()
	{
	}

	/**
	 * Constructor. Creates a model for all feedback messags accepted by the
	 * given filter.
	 * 
	 * @param filter
	 *            The filter to apply
	 */
	public FeedbackMessagesModel(IFeedbackMessageFilter filter)
	{
		setFilter(filter);
	}

	/**
	 * @return The current message filter
	 */
	public final IFeedbackMessageFilter getFilter()
	{
		return filter;
	}

	/**
	 * @see wicket.model.IModel#getNestedModel()
	 */
	public final IModel getNestedModel()
	{
		return null;
	}

	/**
	 * @return The current sorting comparator
	 */
	public final Comparator getSortingComparator()
	{
		return sortingComparator;
	}

	/**
	 * @see wicket.model.AbstractDetachableModel#onGetObject(wicket.Component)
	 */
	public final Object onGetObject(final Component component)
	{
		if (messages == null)
		{
			// Get filtered messages from page where component lives
			List pageMessages = component.getPage().getFeedbackMessages().messages(filter);

			List sessionMessages = component.getSession().getFeedbackMessages().messages(filter);

			messages = new ArrayList(pageMessages.size() + sessionMessages.size());
			messages.addAll(pageMessages);
			messages.addAll(sessionMessages);

			// Sort the list before returning it
			if (sortingComparator != null)
			{
				Collections.sort(messages, sortingComparator);
			}

			// Let subclass do any extra processing it wants to on the messages.
			// It may want to do something special, such as removing a given
			// message under some special condition or perhaps eliminate
			// duplicate messages. It could even add a message under certain
			// conditions.
			messages = processMessages(messages);
		}
		return messages;
	}

	/**
	 * @param filter
	 *            Filter to apply to model
	 */
	public final void setFilter(IFeedbackMessageFilter filter)
	{
		this.filter = filter;
	}

	/**
	 * Sets the comparator used for sorting the messages.
	 * 
	 * @param sortingComparator
	 *            comparator used for sorting the messages
	 */
	public final void setSortingComparator(Comparator sortingComparator)
	{
		if (!(sortingComparator instanceof Serializable))
		{
			throw new IllegalArgumentException("sortingComparator must be serializable");
		}
		this.sortingComparator = sortingComparator;
	}

	/**
	 * @see wicket.model.AbstractDetachableModel#onAttach()
	 */
	protected void onAttach()
	{
	}

	/**
	 * @see wicket.model.AbstractDetachableModel#onDetach()
	 */
	protected void onDetach()
	{
		messages = null;
	}

	/**
	 * @see wicket.model.AbstractDetachableModel#onSetObject(wicket.Component,
	 *      java.lang.Object)
	 */
	protected void onSetObject(Component component, Object object)
	{
	}

	/**
	 * Override this method to post process to the FeedbackMessage list.
	 * 
	 * @param messages
	 *            List of sorted and filtered FeedbackMessages for further
	 *            processing
	 * @return The processed FeedbackMessage list
	 */
	protected List processMessages(final List messages)
	{
		return messages;
	}
}