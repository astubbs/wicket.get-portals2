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
package org.apache.wicket.extensions.markup.html.form.select;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.lang.Objects;
import org.apache.wicket.util.string.Strings;


/**
 * Component that represents a <code>&lt;select&gt;</code> box. Elements are provided by one or
 * more <code>SelectChoice</code> or <code>SelectOptions</code> components in the hierarchy
 * below the <code>Select</code> component.
 * 
 * Advantages to the standard choice components is that the user has a lot more control over the
 * markup between the &lt;select&gt; tag and its children &lt;option&gt; tags: allowing for such
 * things as &lt;optgroup&gt; tags.
 * 
 * TODO Post 1.2: General: Example
 * 
 * @see SelectOption
 * @see SelectOptions
 * 
 * @author Igor Vaynberg
 */
public class Select extends FormComponent
{
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor that will create a default model collection
	 * 
	 * @param id
	 *            component id
	 */
	public Select(String id)
	{
		super(id);
	}

	/**
	 * @see WebMarkupContainer#WebMarkupContainer(String, IModel)
	 */
	public Select(String id, IModel model)
	{
		super(id, model);
	}

	protected void convertInput()
	{
		boolean supportsMultiple = getModelObject() instanceof Collection;

		/*
		 * the input contains an array of full path of the selected option components unless nothing
		 * was selected in which case the input contains null
		 */
		String[] paths = getInputAsArray();

		if (paths == null || paths.length == 0)
		{
			setConvertedInput(null);
			return;
		}

		if (!supportsMultiple && paths.length > 1)
		{
			throw new WicketRuntimeException(
					"The model of Select component [" +
							getPath() +
							"] is not of type java.util.Collection, but more then one SelectOption component has been selected. Either remove the multiple attribute from the select tag or make the model of the Select component a collection");
		}

		List converted = new ArrayList(paths.length);

		/*
		 * if the input is null we do not need to do anything since the model collection has already
		 * been cleared
		 */
		for (int i = 0; i < paths.length; i++)
		{
			String path = paths[i];
			if (!Strings.isEmpty(path))
			{
				/*
				 * option component path sans select component path = relative path from group to
				 * option since we know the option is child of select
				 */
				path = path.substring(getPath().length() + 1);

				// retrieve the selected option component
				SelectOption option = (SelectOption)get(path);

				if (option == null)
				{
					throw new WicketRuntimeException(
							"submitted http post value [" +
									paths.toString() +
									"] for SelectOption component [" +
									getPath() +
									"] contains an illegal relative path element [" +
									path +
									"] which does not point to an SelectOption component. Due to this the Select component cannot resolve the selected SelectOption component pointed to by the illegal value. A possible reason is that component hierarchy changed between rendering and form submission.");
				}
				converted.add(option.getModelObject());
			}

		}

		if (converted.isEmpty())
		{
			setConvertedInput(null);
		}
		else if (!supportsMultiple)
		{
			setConvertedInput(converted.get(0));
		}
		else
		{
			setConvertedInput(converted);
		}
	}


	/**
	 * @see FormComponent#updateModel()
	 */
	public void updateModel()
	{
		Object object = getModelObject();
		boolean supportsMultiple = object instanceof Collection;

		Object converted = getConvertedInput();
		/*
		 * update the model
		 */
		if (supportsMultiple)
		{
			Collection modelCollection = (Collection)object;
			modelChanging();
			modelCollection.clear();
			if (converted != null)
			{
				modelCollection.addAll((Collection)converted);
			}
			modelChanged();
			// force notify of model update via setObject()
			getModel().setObject(modelCollection);
		}
		else
		{
			setModelObject(converted);
		}
	}

	/**
	 * Checks if the specified option is selected
	 * 
	 * @param option
	 * @return true if the option is selected, false otherwise
	 */
	boolean isSelected(SelectOption option)
	{
		// if the raw input is specified use that, otherwise use model
		if (hasRawInput())
		{
			String[] paths = getInputAsArray();
			if (paths != null && paths.length > 0)
			{
				for (int i = 0; i < paths.length; i++)
				{
					String path = paths[i];
					if (path.equals(option.getPath()))
					{
						return true;
					}
				}
			}
		}
		else
		{
			Object selected = getModelObject();
			Object value = option.getModelObject();

			if (selected != null && selected instanceof Collection)
			{
				if (value instanceof Collection)
				{
					return ((Collection)selected).containsAll((Collection)value);
				}
				else
				{
					return ((Collection)selected).contains(value);
				}
			}
			else
			{
				return Objects.equal(selected, value);
			}
		}

		return false;

	}

}
