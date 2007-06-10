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
package org.apache.wicket.markup.html.form;

import org.apache.wicket.Component;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebMarkupContainer;

/**
 * A component that represents html label tag. This component will automatically
 * make the form component output an id attribute and link its for attribute
 * with that value.
 * 
 * @author Igor Vaynberg (ivaynberg)
 */
public class FormComponentLabel extends WebMarkupContainer
{
	private static final long serialVersionUID = 1L;

	private Component component;

	/**
	 * Constructor
	 * 
	 * @param id
	 *            component id
	 * @param check
	 *            Check component that this label represents
	 */
	public FormComponentLabel(String id, Check check)
	{
		this(id, (Component)check);
	}
	
	/**
	 * Constructor
	 * 
	 * @param id
	 *            component id
	 * @param radio
	 *            Radio component that this label represents
	 */
	public FormComponentLabel(String id, Radio radio)
	{
		this(id, (Component)radio);
	}

	/**
	 * Constructor
	 * 
	 * @param id
	 *            component id
	 * @param formComponent
	 *            FormComponent that this label represents
	 */
	public FormComponentLabel(String id, FormComponent formComponent)
	{
		this(id, (Component)formComponent);
	}
	
	private FormComponentLabel(String id, Component component)
	{
		super(id);
		if (component == null)
		{
			throw new IllegalArgumentException("Component argument cannot be null");
		}
		this.component = component;
		component.setOutputMarkupId(true);
	}

	protected void onComponentTag(ComponentTag tag)
	{
		super.onComponentTag(tag);
		checkComponentTag(tag, "label");
		tag.put("for", component.getMarkupId());
	}


	/**
	 * Returns form component bound to this label.
	 * This will be a FormComponent, a Radio or a Check.
	 * 
	 * @return form component
	 */
	public Component getFormComponent()
	{
		return component;
	}
}
