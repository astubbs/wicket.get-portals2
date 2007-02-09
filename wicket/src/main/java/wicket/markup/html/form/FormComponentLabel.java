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
package wicket.markup.html.form;

import wicket.MarkupContainer;
import wicket.markup.ComponentTag;
import wicket.markup.html.WebMarkupContainer;

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

	private FormComponent fc;

	/**
	 * Constructor
	 * 
	 * @param parent
	 *            The parent of this component
	 * 
	 * @param id
	 *            component id
	 * @param formcomponent
	 *            form component that this label represents
	 */
	public FormComponentLabel(MarkupContainer parent, String id, FormComponent formcomponent)
	{
		super(parent, id);
		if (formcomponent == null)
		{
			throw new IllegalArgumentException("Argument `formcomponent` cannot be null");
		}
		this.fc = formcomponent;
		formcomponent.setOutputMarkupId(true);
	}

	@Override
	protected void onComponentTag(ComponentTag tag)
	{
		super.onComponentTag(tag);

		checkComponentTag(tag, "label");
		tag.put("for", fc.getMarkupId());
	}

	/**
	 * Gets the form component this label is attached to
	 * 
	 * @return the form component this label is attached to
	 */
	protected final FormComponent getFormComponent()
	{
		return fc;
	}
}
