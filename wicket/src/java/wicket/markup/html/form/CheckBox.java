/*
 * $Id$
 * $Revision$
 * $Date$
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package wicket.markup.html.form;

import java.io.Serializable;

import wicket.WicketRuntimeException;
import wicket.markup.ComponentTag;

/**
 * HTML checkbox input component.
 *
 * @author Jonathan Locke
 */
public class CheckBox extends FormComponent
{
	/** Serial Version ID. */
	private static final long serialVersionUID = 7559827519977114184L;

	/**
     * @see wicket.Component#Component(String, Serializable)
	 */
	public CheckBox(String name, Serializable object)
	{
		super(name, object);
	}

	/**
     * @see wicket.Component#Component(String, Serializable, String)
	 */
	public CheckBox(String name, Serializable object, String expression)
	{
		super(name, object, expression);
	}

	/**
	 * @see FormComponent#supportsPersistence()
	 */
	protected final boolean supportsPersistence()
	{
		return true;
	}

	/**
	 * @see FormComponent#setValue(java.lang.String)
	 */
	public final void setValue(String value)
	{
		setModelObject(Boolean.valueOf(value));
	}

	/**
	 * Updates this components' model from the request.
	 * @see wicket.markup.html.form.FormComponent#updateModel()
	 */
	protected void updateModel()
	{
		setModelObject(getRequestBoolean());
	}

	/**
	 * Processes the component tag.
	 * @param tag Tag to modify
	 * @see wicket.Component#onComponentTag(ComponentTag)
	 */
	protected final void onComponentTag(final ComponentTag tag)
	{
		checkComponentTag(tag, "input");
		checkComponentTagAttribute(tag, "type", "checkbox");
		super.onComponentTag(tag);

		Object value = getModelObject();

		if (value != null)
		{
			final boolean tagValue;
			if(value instanceof String) // probably was formatted or straight from request
			{
				tagValue = Boolean.valueOf((String)value).booleanValue();
			}
			else if(value instanceof Boolean)
			{
				tagValue = ((Boolean)value).booleanValue();
			}
			else
			{
				throw new WicketRuntimeException(
						"Model objects for checkboxes have to be of type Boolean");
			}
			if(tagValue) tag.put("checked", "checked");
			else tag.remove("checked"); // in case the was a design time attrib
		}
	}
}
