/*
 * $Id: AbstractAjaxBehavior.java 5791 2006-05-20 00:32:57 +0000 (Sat, 20 May
 * 2006) joco01 $ $Revision$ $Date: 2006-05-20 00:32:57 +0000 (Sat, 20
 * May 2006) $
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
package wicket.behavior;

import wicket.Component;
import wicket.markup.ComponentTag;

/**
 * A lightweight version of the attribute modifier. This is convenient for
 * simpler situations where you know the value upfront and you do not need a
 * pull-based model.
 * 
 * @author Igor Vaynberg (ivaynberg)
 * @deprecated moved to wicket-extensions
 */
@Deprecated
public class SimpleAttributeModifier extends AbstractBehavior
{
	private static final long serialVersionUID = 1L;

	/** The attribute */
	private String attribute;

	/** The value to set */
	private CharSequence value;

	/**
	 * Construct.
	 * 
	 * @param attribute
	 *            The attribute
	 * @param value
	 *            The value
	 */
	public SimpleAttributeModifier(final String attribute, final CharSequence value)
	{
		if (attribute == null)
		{
			throw new IllegalArgumentException("Argument [attr] cannot be null");
		}
		if (value == null)
		{
			throw new IllegalArgumentException("Argument [value] cannot be null");
		}
		this.attribute = attribute;
		this.value = value;
	}

	/**
	 * @see wicket.behavior.AbstractBehavior#onComponentTag(wicket.Component,
	 *      wicket.markup.ComponentTag)
	 */
	@Override
	public void onComponentTag(final Component component, final ComponentTag tag)
	{
		if (isEnabled())
		{
			tag.getAttributes().put(attribute, value);
		}
	}

	/**
	 * @return True to enable the modifier, false to disable
	 */
	protected boolean isEnabled()
	{
		return true;
	}
}
