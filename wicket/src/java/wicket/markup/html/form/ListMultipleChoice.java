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
package wicket.markup.html.form;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import wicket.markup.ComponentTag;
import wicket.model.IModel;
import wicket.util.string.Strings;

/**
 * A multiple choice list component.
 * 
 * @author Jonathan Locke
 * @author Johan Compagner
 */
public class ListMultipleChoice extends AbstractChoice
{
	/**
	 * @see wicket.markup.html.form.AbstractChoice#AbstractChoice(String)
	 */
	public ListMultipleChoice(final String id)
	{
		super(id);
	}

	/**
	 * @see wicket.markup.html.form.AbstractChoice#AbstractChoice(String, Collection)
	 */
	public ListMultipleChoice(final String id, final Collection choices)
	{
		super(id, choices);
	}

	/**
	 * @see wicket.markup.html.form.AbstractChoice#AbstractChoice(String, IChoiceRenderer,Collection)
	 */
	public ListMultipleChoice(final String id, final IChoiceRenderer renderer, final Collection choices)
	{
		super(id, renderer,choices);
	}

	/**
	 * @see wicket.markup.html.form.AbstractChoice#AbstractChoice(String, IModel, Collection)
	 */
	public ListMultipleChoice(final String id, IModel object, final Collection choices)
	{
		super(id, object, choices);
	}

	/**
	 * @see wicket.markup.html.form.AbstractChoice#AbstractChoice(String, IModel, IChoiceRenderer,Collection)
	 */
	public ListMultipleChoice(final String id, IModel object, final IChoiceRenderer renderer, final Collection choices)
	{
		super(id, object, choices);
	}

	/**
	 * @see FormComponent#getModelValue()
	 */
	public final String getModelValue()
	{
		// Get the list of selected values
		final Collection selectedValues = (Collection)getModelObject();
		final StringBuffer buffer = new StringBuffer();
		if (selectedValues != null)
		{
			final Collection choices = getChoices();
			for (final Iterator iterator = selectedValues.iterator(); iterator.hasNext();)
			{
				final Object object = iterator.next();
				
				int index = -1;
				if(choices instanceof List)
				{
					index = ((List)choices).indexOf(object);
				}
				else
				{
					Iterator it = choices.iterator();
					while(it.hasNext())
					{
						index++;
						if(it.next().equals(object)) break;
					}
				}
				buffer.append(getChoiceRenderer().getIdValue(object, index));
				buffer.append(";");
			}
		}
		return buffer.toString();
	}

	/**
	 * @see FormComponent#setModelValue(java.lang.String)
	 */
	public final void setModelValue(final String value)
	{
		Collection selectedValues = (Collection)getModelObject();
		if (selectedValues == null)
		{
			selectedValues = new ArrayList();
			setModelObject(selectedValues);
		}
		else
		{
			selectedValues.clear();
		}
		final Collection choices = getChoices();
		for (final StringTokenizer tokenizer = new StringTokenizer(value, ";"); tokenizer
				.hasMoreTokens();)
		{
			String selected = tokenizer.nextToken();
			
			Iterator it = getChoices().iterator();
			int index = -1;
			while(it.hasNext())
			{
				index++;
				Object object = it.next();
				if(getChoiceRenderer().getIdValue(object, index).equals(selected))
				{
					selectedValues.add(object);
					break;
				}
			}
			
		}
	}

	/**
	 * @see wicket.markup.html.form.AbstractChoice#isSelected(Object,int)
	 */
	protected final boolean isSelected(Object choice, int index)
	{
		// Get value of the form "id1;id2;id3"
		final String value = getValue();
		
		// Have a value at all?
		if (value != null)
		{
			// Loop through ids
			for (final StringTokenizer tokenizer = new StringTokenizer(value, ";"); tokenizer
					.hasMoreTokens();)
			{
				final String id = tokenizer.nextToken(); 
				if (id.equals(getChoiceRenderer().getIdValue(choice, index)))
				{
					return true;
				}
			}

		}
		return false;
	}

	/**
	 * @see wicket.Component#onComponentTag(ComponentTag)
	 */
	protected final void onComponentTag(final ComponentTag tag)
	{
		super.onComponentTag(tag);
		tag.put("multiple", true);
	}

	/**
	 * @see FormComponent#updateModel()
	 */
	protected final void updateModel()
	{
		// Get the list of selected values
		Collection selectedValues = (Collection)getModelObject();

		if (selectedValues != null)
		{
			selectedValues.clear();
		}
		else
		{
			selectedValues = new ArrayList();
			setModelObject(selectedValues);
		}

		// Get indices selected from request
		final String[] ids = inputAsStringArray();

		// If one or more ids is selected
		if (ids != null && ids.length > 0 && !Strings.isEmpty(ids[0]))
		{
			// Get values that could be selected
			final Collection choices = getChoices();

			// Loop through selected indices
			for (int i = 0; i < ids.length; i++)
			{
				Iterator it = getChoices().iterator();
				int index = -1;
				while(it.hasNext())
				{
					index++;
					Object object = it.next();
					if(getChoiceRenderer().getIdValue(object, index).equals(ids[i]))
					{
						selectedValues.add(object);
						break;
					}
				}
			}
		}
	}
}