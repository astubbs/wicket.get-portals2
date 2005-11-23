/*
 * $Id$ $Revision$
 * $Date$
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
package wicket.extensions.markup.html.form.select;

import java.util.Collection;

import wicket.Component;
import wicket.WicketRuntimeException;
import wicket.markup.ComponentTag;
import wicket.markup.html.WebMarkupContainer;
import wicket.model.IModel;

/**
 * Component representing a single <code>&lt;option&gt;</code> html element
 * 
 * TODO example
 *  
 * @see Select
 * 
 * @author Igor Vaynberg (ivaynberg@users.sf.net)
 * 
 */
public class SelectOption extends WebMarkupContainer
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * @see WebMarkupContainer#WebMarkupContainer(String)
	 */
	public SelectOption(String id)
	{
		super(id);
	}

	/**
	 * @see WebMarkupContainer#WebMarkupContainer(String, IModel)
	 */
	public SelectOption(String id, IModel model)
	{
		super(id, model);
	}


	/**
	 * @see Component#onComponentTag(ComponentTag)
	 * @param tag
	 *            the abstraction representing html tag of this component
	 */
	protected void onComponentTag(final ComponentTag tag)
	{

		// must be attached to <option .../> tag
		checkComponentTag(tag, "option");

		Select select = (Select)findParent(Select.class);
		if (select == null)
		{
			throw new WicketRuntimeException(
					"SelectOption component ["
							+ getPath()
							+ "] cannot find its parent Select. All SelectOption components must be a child of or below in the hierarchy of a Select component.");
		}

		// assign name and value
		tag.put("value", getPath());

		// check if the model collection of the select contains the model object.
		// if it does mark the option as selected
		Object selected=select.getModelObject();

		// check for npe in select's model object
		if (selected == null)
		{
			throw new WicketRuntimeException(
					"Select ["
							+ select.getPath()
							+ "] contains a null model object, must be an object of type java.lang.Object or java.util.Collection (in case of multi-valued select element");
		}

		boolean isSelected=false;
		
		Object value=getModelObject();
		
		if (selected instanceof Collection) {
			
			if (value instanceof Collection) {
				isSelected=selected.equals(value);
			} else {
				isSelected=((Collection)selected).contains(value);
			}
		} else {
			isSelected=selected.equals(value);
		}
		
		if (isSelected)
		{
			tag.put("selected", "true");
		}

		// Default handling for component tag
		super.onComponentTag(tag);
	}


}
