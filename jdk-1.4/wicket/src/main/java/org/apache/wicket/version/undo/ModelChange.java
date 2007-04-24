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
package org.apache.wicket.version.undo;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.lang.Classes;
import org.apache.wicket.util.lang.Objects;


/**
 * A model change operation.
 * 
 * @author Jonathan Locke
 */
class ModelChange extends Change
{
	private static final long serialVersionUID = 1L;

	/** log. */
	private static final Log log = LogFactory.getLog(ModelChange.class);

	/** subject. */
	private final Component component;

	/** original model. */
	private IModel originalModel;

	/**
	 * Construct.
	 * 
	 * @param component
	 *            subject of the change
	 */
	ModelChange(final Component component)
	{
		if (component == null)
		{
			throw new IllegalArgumentException("argument component must be not null");
		}

		// Save component
		this.component = component;

		// Get component model
		final IModel model = component.getModel();

		// If the component has a model, it's about to change!
		if (model != null)
		{
			// Should we clone the model?
			boolean cloneModel = true;

			// If the component is a form component
			if (model instanceof CompoundPropertyModel)
			{
				if (component instanceof FormComponent)
				{
					// and it's using the same model as the form
					if (((FormComponent)component).getForm().getModel() == model)
					{
						// we don't need to clone the model, because it will
						// be re-initialized using initModel()
						cloneModel = false;
					}
				}
				else
				{
					// If the component is using the same model as the page
					if (component.getPage().getModel() == model)
					{
						// we don't need to clone the model, because it will
						// be re-initialized using initModel()
						cloneModel = false;
					}
				}
			}

			// Clone model?
			if (cloneModel)
			{
				model.detach();
				originalModel = (IModel)Objects.cloneModel(model);
			}
			else
			{
				originalModel = model;
			}
		}

		if (log.isDebugEnabled())
		{
			log.debug("RECORD MODEL CHANGE: changed model of " + " ("
					+ Classes.simpleName(component.getClass()) + "@" + component.hashCode() + ")");
		}
	}

	/**
	 * @see org.apache.wicket.version.undo.Change#undo()
	 */
	public void undo()
	{
		if (log.isDebugEnabled())
		{
			log.debug("UNDO MODEL CHANGE: setting original model " + originalModel + " to "
					+ component.getPath() + "@" + component.hashCode() + ")");
		}

		component.setModel(originalModel);
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString()
	{
		return "ModelChange[component: " + component.getPath() + "]";
	}
}