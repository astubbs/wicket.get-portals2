/*
 * $Id: WicketComponentTagIdentifier.java,v 1.4 2005/02/04 07:22:53 jdonnerstag
 * Exp $ $Revision$ $Date$
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
package wicket.markup.parser.filter;

import java.text.ParseException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import wicket.markup.ComponentTag;
import wicket.markup.ComponentWicketTag;
import wicket.markup.MarkupElement;
import wicket.markup.parser.AbstractMarkupFilter;
import wicket.markup.parser.IMarkupFilter;
import wicket.markup.parser.XmlTag;

/**
 * This is a markup inline filter. It identifies xml tags which have a special
 * meaning for Wicket. There are two type of tags which have a special meaning
 * for Wicket.
 * <p>
 * <ul>
 * <li>All tags with Wicket namespace, e.g. &lt;wicket:remove&gt;</li>
 * <li>All tags with an attribute like id="wicket-myLabel" or wicket="myLabel"
 * </li>
 * </ul>
 * 
 * @author Juergen Donnerstag
 */
public final class WicketComponentTagIdentifier extends AbstractMarkupFilter
{
	/** Logging */
	private static final Log log = LogFactory.getLog(WicketComponentTagIdentifier.class);

	/** Name of desired componentName tag attribute. */
	private String componentNameAttribute = ComponentTag.DEFAULT_COMPONENT_NAME_ATTRIBUTE;

	/** if true, "wicket-" will be removed from id="wicket-xxx" */
	private boolean stripWicketFromComponentTag = false;

	/**
	 * Construct.
	 * 
	 * @param parent
	 *            The next MarkupFilter in the chain
	 */
	public WicketComponentTagIdentifier(final IMarkupFilter parent)
	{
		super(parent);
	}

	/**
	 * Name of the desired componentName tag attribute.
	 * 
	 * @param name
	 *            component name
	 */
	public void setComponentNameAttribute(final String name)
	{
		this.componentNameAttribute = name;

		if (!ComponentTag.DEFAULT_COMPONENT_NAME_ATTRIBUTE.equals(componentNameAttribute))
		{
			log.info("You are using a non-standard component name: " + componentNameAttribute);
		}
	}

	/**
	 * If true, "wicket-" will be removed from component tag's id attributes.
	 * E.g. id="wicket-myLabel" will be id="myLabel" on the output.
	 * 
	 * @param enable
	 *            if true, remove "wicket-"
	 */
	public void setStripWicketFromComponentTag(final boolean enable)
	{
		this.stripWicketFromComponentTag = enable;
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
		XmlTag xmlTag = (XmlTag)getParent().nextTag();
		if (xmlTag == null)
		{
			return xmlTag;
		}

		// Identify tags with Wicket namespace
		ComponentTag tag;
		if (componentNameAttribute.equalsIgnoreCase(xmlTag.getNamespace())
				|| ComponentTag.DEFAULT_COMPONENT_NAME_ATTRIBUTE.equalsIgnoreCase(xmlTag
						.getNamespace()))
		{
			// It is <wicket:...>
			tag = new ComponentWicketTag(xmlTag);

			// Make it a wicket component. Otherwise it would be RawMarkup
			tag.setComponentName(tag.getName());
		}
		else
		{
			// Everything else, except tags with Wicket namespace
			tag = new ComponentTag(xmlTag);
		}

		// If the form <tag id = "wicket-value"> is used
		final String id = xmlTag.getAttributes().getString("id");

		if ((id != null) && id.startsWith(componentNameAttribute + "-"))
		{
			// extract component name from value
			tag.setComponentName(id.substring(componentNameAttribute.length() + 1).trim());

			// Depending on apps setting, "wicket-" will be removed or not
			if (this.stripWicketFromComponentTag)
			{
				tag.put("id", tag.getComponentName());
			}
		}
		else if ((id != null) && id.startsWith(ComponentTag.DEFAULT_COMPONENT_NAME_ATTRIBUTE))
		{
			// extract component name from value
			tag.setComponentName(id.substring(
					ComponentTag.DEFAULT_COMPONENT_NAME_ATTRIBUTE.length() + 1).trim());

			// Depending on apps setting, "wicket-" will be removed or not
			if (this.stripWicketFromComponentTag)
			{
				tag.put("id", tag.getComponentName());
			}
		}
		else if (tag.getAttributes().containsKey(componentNameAttribute))
		{
			// Set componentName value on tag
			tag.setComponentName(tag.getAttributes().getString(componentNameAttribute));
		}
		else if (tag.getAttributes().containsKey(ComponentTag.DEFAULT_COMPONENT_NAME_ATTRIBUTE))
		{
			// Set componentName value on tag
			tag.setComponentName(tag.getAttributes().getString(
					ComponentTag.DEFAULT_COMPONENT_NAME_ATTRIBUTE));
		}

		return tag;
	}
}
