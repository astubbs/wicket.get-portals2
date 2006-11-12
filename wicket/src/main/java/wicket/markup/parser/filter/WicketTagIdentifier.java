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
package wicket.markup.parser.filter;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import wicket.Component;
import wicket.markup.ComponentTag;
import wicket.markup.IMarkup;
import wicket.markup.MarkupElement;
import wicket.markup.parser.AbstractMarkupFilter;
import wicket.markup.parser.XmlTag;

/**
 * This is a markup inline filter. It identifies xml tags which have a special
 * meaning for Wicket. There are two type of tags which have a special meaning
 * for Wicket.
 * <p>
 * <ul>
 * <li>All tags with Wicket namespace, e.g. &lt;wicket:remove&gt;</li>
 * <li>All tags with an attribute like wicket:id="myLabel" </li>
 * </ul>
 * 
 * @author Juergen Donnerstag
 */
public final class WicketTagIdentifier extends AbstractMarkupFilter
{
	/** List of well known wicket tag namses */
	private static List<String> wellKnownTagNames = new ArrayList<String>();

	/** wicket tag which require unique ids */
	private static List<String> requiresUniqueId = new ArrayList<String>();

//	static
//	{
//		// register wicket:head, wicket:extend, etc.
//		registerTagWhichRequiresUniqueId("head");
//	}

	/** The current markup needed to get the markups namespace */
	private final IMarkup markup;

	/** auto increment to create unique ids */
	private int index;

	/**
	 * Construct.
	 * 
	 * @param markup
	 *            The markup as known by now
	 */
	public WicketTagIdentifier(final IMarkup markup)
	{
		this.markup = markup;
	}

	/**
	 * Get the next tag from the next MarkupFilter in the chain and search for
	 * Wicket specific tags.
	 * <p>
	 * Note: The xml parser - the next MarkupFilter in the chain - returns
	 * XmlTags which are a subclass of MarkupElement. The implementation of this
	 * filter will return either ComponentTags or ComponentWicketTags. Both are
	 * subclasses of MarkupElement as well and both maintain a reference to the
	 * XmlTag. But no XmlTag is returned.
	 * 
	 * @see wicket.markup.parser.IMarkupFilter#nextTag()
	 * @return The next tag from markup to be processed. If null, no more tags
	 *         are available
	 */
	public MarkupElement nextTag() throws ParseException
	{
		// Get the next tag from the markup.
		// If null, no more tags are available
		final XmlTag xmlTag = (XmlTag)getParent().nextTag();
		if (xmlTag == null)
		{
			return xmlTag;
		}

		final String namespace = this.markup.getWicketNamespace();

		// Identify tags with Wicket namespace
		ComponentTag tag = new ComponentTag(xmlTag);
		if (namespace.equalsIgnoreCase(xmlTag.getNamespace()))
		{
			// It is <wicket:...>
			tag.setWicketTag(true);

			// Make it a wicket component. Otherwise it would be RawMarkup
			String id = Component.AUTO_COMPONENT_PREFIX + tag.getName();
			if (requiresUniqueId.contains(tag.getName()))
			{
				id = id + this.index++;
			}
			tag.setId(id);

			if (isRegisteredToHaveUniqueId(tag) == false)
			{
				throw new ParseException("Unkown tag name with Wicket namespace: '"
						+ xmlTag.getName()
						+ "'. Might be you haven't installed the appropriate resolver?", tag
						.getPos());
			}
		}

		// If the form <tag wicket:id = "value"> is used
		final String value = tag.getAttributes().getString(namespace + ":id");
		if (value != null)
		{
			if (value.trim().length() == 0)
			{
				throw new ParseException(
						"The wicket:id attribute value must not be empty. May be unmatched quotes?!?",
						tag.getPos());
			}
			// Make it a wicket component. Otherwise it would be RawMarkup
			tag.setId(value);
		}

		return tag;
	}

	/**
	 * Register a new well known wicket tag name (e.g. panel)
	 * 
	 * @param name
	 */
	public final static void registerWellKnownTagName(final String name)
	{
		if (wellKnownTagNames.contains(name) == false)
		{
			wellKnownTagNames.add(name);
		}
	}

	/**
	 * Register tags, such as wicket:head, which require unique ids.
	 * 
	 * @param tag
	 */
	public final static void registerTagWhichRequiresUniqueId(final String tag)
	{
		requiresUniqueId.add(tag);
	}

	/**
	 * 
	 * @param tag
	 *            The tag (name) to check
	 * @return True, if tag is is required to be unique
	 */
	public final static boolean isRegisteredToHaveUniqueId(final ComponentTag tag)
	{
		return wellKnownTagNames.contains(tag.getName());
	}
}
