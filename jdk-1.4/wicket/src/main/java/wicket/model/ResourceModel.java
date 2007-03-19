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
package wicket.model;

import wicket.Application;
import wicket.Component;

/**
 * A model that represents a localized resource string. This is a lightweight
 * version of the {@link StringResourceModel}. It lacks parameter
 * substitutions, but is generaly easier to use.
 * 
 * @author Igor Vaynberg (ivaynberg)
 * 
 */
public class ResourceModel extends AbstractReadOnlyModel implements IAssignmentAwareModel
{
	private static final long serialVersionUID = 1L;

	private String resourceKey;

	private String defaultValue;

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
	 * @see wicket.model.AbstractReadOnlyModel#getObject()
	 */
	public Object getObject()
	{
		// this shouldn't be called always wrapped!
		return Application.get().getResourceSettings().getLocalizer().getString(resourceKey,
				(Component)null, defaultValue);
	}


	/**
	 * @see wicket.model.IAssignmentAwareModel#wrapOnAssignment(wicket.Component)
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

		private Component component;

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
		 * @see wicket.model.IWrapModel#getNestedModel()
		 */
		public IModel getNestedModel()
		{
			return ResourceModel.this;
		}

		/**
		 * @see wicket.model.AbstractReadOnlyModel#getObject()
		 */
		public Object getObject()
		{
			return Application.get().getResourceSettings().getLocalizer().getString(resourceKey,
					component, defaultValue);
		}
	}
}
