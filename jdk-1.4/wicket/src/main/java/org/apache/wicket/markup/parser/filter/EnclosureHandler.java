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
package org.apache.wicket.markup.parser.filter;

import java.text.ParseException;
import java.util.Stack;

import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.MarkupElement;
import org.apache.wicket.markup.WicketTag;
import org.apache.wicket.markup.html.internal.Enclosure;
import org.apache.wicket.markup.parser.AbstractMarkupFilter;
import org.apache.wicket.markup.resolver.EnclosureResolver;


/**
 * This is a markup inline filter. It identifies &lt;wicket:enclosure&gt; tags.
 * If the 'child' attribute is empty it determines the wicket:id of the child
 * component automatically by analysing the org.apache.wicket component (in this case on
 * one org.apache.wicket component is allowed) in between the open and close tags. If the
 * enclosure tag has a 'child' attribute like
 * <code>&lt;wicket:enclosure child="xxx"&gt;</code> than more than just one
 * org.apache.wicket component inside the enclosure tags are allowed and the child
 * component which determines the visibility of the enclosure is identified by
 * the 'child' attribute value which must be equal to the relative child id
 * path.
 * 
 * @see EnclosureResolver
 * @see Enclosure
 * 
 * @author Juergen Donnerstag
 */
public final class EnclosureHandler extends AbstractMarkupFilter
{
	/** The child attribute */
	public static final String CHILD_ATTRIBUTE = "child";

	static
	{
		// register "wicket:enclosure"
		WicketTagIdentifier.registerWellKnownTagName("enclosure");
	}

	/** Stack of <wicket:enclosure> tags */
	private Stack/* <ComponentTag> */stack;

	/** The id of the first org.apache.wicket tag inside the enclosure */
	private String childId;

	/**
	 * Construct.
	 */
	public EnclosureHandler()
	{
	}

	/**
	 * @see org.apache.wicket.markup.parser.IMarkupFilter#nextTag()
	 */
	public final MarkupElement nextTag() throws ParseException
	{
		// Get the next tag from the next MarkupFilter in the chain.
		// If null, no more tags are available
		final ComponentTag tag = nextComponentTag();
		if (tag == null)
		{
			return tag;
		}

		final boolean isWicketTag = tag instanceof WicketTag;
		final boolean isEnclosureTag = isWicketTag && ((WicketTag)tag).isEnclosureTag();

		// If wicket:enclosure
		if (isEnclosureTag)
		{
			// If open tag, than put the tag onto the stack
			if (tag.isOpen())
			{
				if (this.stack == null)
				{
					this.stack = new Stack/* <ComponentTag> */();
				}
				this.stack.push(tag);
			}
			// If close tag, than remove the tag from the stack and update
			// the child attribute of the open tag if required
			else if (tag.isClose())
			{
				if (this.stack == null)
				{
					throw new ParseException("Missing open tag for Enclosure: " + tag.toString(),
							tag.getPos());
				}

				// Remove the open tag from the stack
				ComponentTag lastEnclosure = (ComponentTag)this.stack.pop();

				// If the child attribute has not been given by the user,
				// than ...
				if (this.childId != null)
				{
					lastEnclosure.put(CHILD_ATTRIBUTE, this.childId);
					lastEnclosure.setModified(true);
					this.childId = null;
				}

				if (this.stack.size() == 0)
				{
					this.stack = null;
				}
			}
			else
			{
				throw new ParseException("Open-close tag not allowed for Enclosure: "
						+ tag.toString(), tag.getPos());
			}
		}
		// Are we inside a wicket:enclosure tag?
		else if ((tag.getId() != null) && (isWicketTag == false) && (stack != null))
		{
			ComponentTag lastEnclosure = (ComponentTag)this.stack.lastElement();

			// If the enclosure tag has NO child attribute, than ...
			if (lastEnclosure.getString(CHILD_ATTRIBUTE) == null)
			{
				// We encountered more than one child component inside
				// the enclosure and are not able to automatically
				// determine the child component to delegate the
				// isVisible() to => Exception
				if (this.childId != null)
				{
					throw new ParseException(
							"Use <wicket:enclosure child='xxx'> to name the child component", tag
									.getPos());
				}
				// Remember the child id. The open tag will be updated
				// once the close tag is found. See above.
				this.childId = tag.getId();
			}
		}

		return tag;
	}
}
