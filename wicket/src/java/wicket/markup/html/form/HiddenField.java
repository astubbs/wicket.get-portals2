/*
 * $Id$ $Revision:
 * 1.10 $ $Date$
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
package wicket.markup.html.form;

import wicket.markup.ComponentTag;
import wicket.model.IModel;

/**
 * TextField doesn't permit the html <input type='hidden'> so this is a simple subclass to allow this
 * 
 * A HiddenField is useful when you have a javascript based component that updates the form state.
 * Either 
 * 
 * 1) add a AttributeModified to set the id attribute, then use document.getElementById(id), or
 * 2) lookup the field name=getPath() within the form  
 * 
 * @author Cameron Braid
 */
public class HiddenField extends TextField
{
	private static final long serialVersionUID = 1L;
	
	/**
	 * Construct.
	 * @param id component id
	 */
	public HiddenField(String id)
	{
		super(id);
	}

	/**
	 * Construct.
	 * @param id component id
	 * @param type the type to use when updating the model for this text field
	 */
	public HiddenField(String id, Class type)
	{
		super(id, type);
	}

	/**
	 * @param id component id
	 * @param model the model
	 * @param type the type to use when updating the model for this text field
	 * @see wicket.Component#Component(String, IModel)
	 */
	public HiddenField(String id, IModel model, Class type)
	{
		super(id, model, type);
	}

	/**
	 * Construct.
	 * @param id see Component
	 * @param model the model
	 */
	public HiddenField(String id, IModel model)
	{
		super(id, model);
	}

	/**
	 * @see wicket.Component#onComponentTag(wicket.markup.ComponentTag)
	 */
	protected void onComponentTag(ComponentTag tag)
	{
		// Check for hidden type
		checkComponentTagAttribute(tag, "type", "hidden");

		// Default handling for component tag
		super.onComponentTag(tag);
	}
}
