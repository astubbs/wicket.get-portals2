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
package wicket.extensions.markup.html.form.select;

import java.util.Collection;

import wicket.MarkupContainer;
import wicket.WicketRuntimeException;
import wicket.markup.html.form.FormComponent;
import wicket.model.IModel;
import wicket.util.lang.Objects;

/**
 * Component that serves as the base for {@link Select} and {@link SelectMultiple}.
 * 
 * @see Select
 * @see SelectMultiple
 * 
 * @param <T> type of model object
 * 
 * @author Igor Vaynberg (ivaynberg@users.sf.net)
 * @author Matej Knopp 
 */
public abstract class AbstractSelect<T> extends FormComponent<T>
{
	private static final long serialVersionUID = 1L;

	/**
	 * @see wicket.Component#Component(MarkupContainer,String)
	 */
	public AbstractSelect(MarkupContainer parent, final String id)
	{
		super(parent, id);
	}

	/**
	 * @see wicket.Component#Component(MarkupContainer, String, IModel)
	 */
	public AbstractSelect(MarkupContainer parent, final String id, IModel<T> model)
	{
		super(parent, id, model);
	}

	/**
	 * Clears the model. Either by setting the model object to null (single selection
	 * or cleaning the collection (select multiple).	
	 */
	protected abstract void clearModel();

	/**
	 * Assigns the with the current model.
	 * @param value 
	 * 			value of selected choice
	 */
	protected abstract void assignValue(Object value);
	
	/**
	 * Checks whether the given count of options selected by user is correct
	 * for this type of Select.
	 * @param count 
	 * 			count of options selected by user
	 */
	protected abstract void checkSelectedOptionsCount(int count);
	
	/**
	 * This methods is called after the model has been updated.
	 */
	protected abstract void finishModelUpdate();
	
	/**
	 * @see FormComponent#updateModel()
	 */
	@Override
	public void updateModel()
	{
		clearModel();

		/*
		 * the input contains an array of full path of the selected option
		 * components unless nothing was selected in which case the input
		 * contains null
		 */
		String[] paths = getInputAsArray();

		/*
		 * if the input is null we do not need to do anything since the model
		 * collection has already been cleared
		 */

		if (paths != null && paths.length > 0)
		{
			checkSelectedOptionsCount(paths.length);

			for (String element : paths)
			{
				String path = element;
				if (path != null)
				{
					/*
					 * option component path sans select component path =
					 * relative path from group to option since we know the
					 * option is child of select
					 */
					path = path.substring(getPath().length() + 1);

					// retrieve the selected checkbox component
					SelectOption option = (SelectOption)get(path);
					if (option == null)
					{
						throw new WicketRuntimeException(
								"submitted http post value ["
										+ paths.toString()
										+ "] for SelectOption component ["
										+ getPath()
										+ "] contains an illegal relative path element ["
										+ path
										+ "] which does not point to an SelectOption component. Due to this the Select component cannot resolve the selected SelectOption component pointed to by the illegal value. A possible reason is that component hierarchy changed between rendering and form submission.");
					}

					assignValue(option.getModelObject());
				}
			}
		}

		finishModelUpdate();
	}
	
	/**
	 * Checks if the specified option is selected
	 * 
	 * @param option
	 * @return true if the option is selected, false otherwise
	 */
	@SuppressWarnings("unchecked")
	boolean isSelected(SelectOption option)
	{
		// if the raw input is specified use that, otherwise use model
		if (hasRawInput()) {
			String[] paths = getInputAsArray();
			if (paths != null && paths.length > 0)
			{
				for (int i = 0; i < paths.length; i++)
				{
					String path = paths[i];
					if (path.equals(option.getPath())) {
						return true;
					}
				}
			}
		} else {
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
