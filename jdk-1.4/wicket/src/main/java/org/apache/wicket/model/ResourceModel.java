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
package org.apache.wicket.model;

import org.apache.wicket.Application;
import org.apache.wicket.Component;

/**
 * A model that represents a localized resource string. This is a lightweight
 * version of the {@link StringResourceModel}. It lacks parameter
 * substitutions, but is generaly easier to use.
 * <p>
 * If you don't use this model as primary component model (you don't specify it
 * in component constructor and don't assign it to component using
 * {@link Component#setModel(IModel)}), you will need to connect the model
 * with a component using {@link #wrapOnAssignment(Component)}.
 * 
 * @author Igor Vaynberg (ivaynberg)
 * 
 */
public class ResourceModel extends AbstractReadOnlyModel implements IComponentAssignedModel
{
	private static final long serialVersionUID = 1L;

	private final String resourceKey;

	private final String defaultValue;

	/**
	 * Constructor
	 * 
	 * @param resourceKey
	 *            key of the resource this model represents
	 */
	public ResourceModel(String resourceKey)
	{
		this(resourceKey, null);
	}

	/**
	 * Constructor
	 * 
	 * @param resourceKey
	 *            key of the resource this model represents
	 * @param defaultValue
	 *            value that will be returned if resource does not exist
	 * 
	 */
	public ResourceModel(String resourceKey, String defaultValue)
	{
		this.resourceKey = resourceKey;
		this.defaultValue = defaultValue;
	}

	/**
	 * @see org.apache.wicket.model.AbstractReadOnlyModel#getObject()
	 */
	public Object getObject()
	{
		// this shouldn't be called always wrapped!
		return Application.get().getResourceSettings().getLocalizer().getString(resourceKey,
				(Component)null, defaultValue);
	}


	/**
	 * @see org.apache.wicket.model.IComponentAssignedModel#wrapOnAssignment(org.apache.wicket.Component)
	 */
	public IWrapModel wrapOnAssignment(final Component component)
	{
		return new AssignmentWrapper(resourceKey, defaultValue, component);
	}

	/**
	 * 
	 */
	private class AssignmentWrapper extends ResourceModel implements IWrapModel
	{
		private static final long serialVersionUID = 1L;

		private final Component component;

		/**
		 * Construct.
		 * 
		 * @param resourceKey
		 * @param defaultValue
		 * @param component
		 */
		public AssignmentWrapper(String resourceKey, String defaultValue, Component component)
		{
			super(resourceKey, defaultValue);
			this.component = component;
		}

		/**
		 * @see org.apache.wicket.model.IWrapModel#getWrappedModel()
		 */
		public IModel getWrappedModel()
		{
			return ResourceModel.this;
		}

		/**
		 * @see org.apache.wicket.model.AbstractReadOnlyModel#getObject()
		 */
		public Object getObject()
		{
			return Application.get().getResourceSettings().getLocalizer().getString(resourceKey,
					component, defaultValue);
		}
	}
}
