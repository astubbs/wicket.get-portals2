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
package org.apache.wicket.extensions.ajax.markup.html;

import java.util.List;

import org.apache.wicket.Application;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.MarkupStream;
import org.apache.wicket.markup.html.WebComponent;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.convert.IConverter;
import org.apache.wicket.util.string.Strings;


/**
 * An inplace editor much like {@link AjaxEditableLabel}, but instead of a {@link TextField} a
 * {@link DropDownChoice} is displayed.
 * 
 * @param <T>
 * @author Eelco Hillenius
 */
public class AjaxEditableChoiceLabel<T> extends AjaxEditableLabel<T>
{
	private static final long serialVersionUID = 1L;

	/** The list of objects. */
	private IModel<? extends List<? extends T>> choices;

	/** The renderer used to generate display/id values for the objects. */
	private IChoiceRenderer<T> renderer;

	/**
	 * Construct.
	 * 
	 * @param id
	 *            The component id
	 */
	public AjaxEditableChoiceLabel(String id)
	{
		super(id);
	}

	/**
	 * Construct.
	 * 
	 * @param id
	 *            The component id
	 * @param model
	 *            The model
	 */
	public AjaxEditableChoiceLabel(String id, IModel<T> model)
	{
		super(id, model);
	}

	/**
	 * Construct.
	 * 
	 * @param id
	 *            The component id
	 * @param choices
	 *            The collection of choices in the dropdown
	 */
	public AjaxEditableChoiceLabel(String id, List<? extends T> choices)
	{
		this(id, null, choices);
	}

	/**
	 * Construct.
	 * 
	 * @param id
	 *            The component id
	 * @param model
	 *            The model
	 * @param choices
	 *            The collection of choices in the dropdown
	 */
	public AjaxEditableChoiceLabel(String id, IModel<T> model,
		IModel<? extends List<? extends T>> choices)
	{
		super(id, model);
		this.choices = choices;
	}

	/**
	 * Construct.
	 * 
	 * @param id
	 *            The component id
	 * @param model
	 *            The model
	 * @param choices
	 *            The collection of choices in the dropdown
	 * @param renderer
	 *            The rendering engine
	 */
	public AjaxEditableChoiceLabel(String id, IModel<T> model,
		IModel<? extends List<? extends T>> choices, IChoiceRenderer<T> renderer)
	{
		super(id, model);
		this.choices = choices;
		this.renderer = renderer;
	}

	/**
	 * Construct.
	 * 
	 * @param id
	 *            The component id
	 * @param model
	 *            The model
	 * @param choices
	 *            The collection of choices in the dropdown
	 */
	@SuppressWarnings("unchecked")
	public AjaxEditableChoiceLabel(String id, IModel<T> model, List<? extends T> choices)
	{
		this(id, model, Model.of(choices));
	}

	/**
	 * Construct.
	 * 
	 * @param id
	 *            The component id
	 * @param model
	 *            The model
	 * @param choices
	 *            The collection of choices in the dropdown
	 * @param renderer
	 *            The rendering engine
	 */
	@SuppressWarnings("unchecked")
	public AjaxEditableChoiceLabel(String id, IModel<T> model, List<? extends T> choices,
		IChoiceRenderer<T> renderer)
	{
		this(id, model, Model.of(choices), renderer);
	}


	/**
	 * @see org.apache.wicket.extensions.ajax.markup.html.AjaxEditableLabel#newEditor(org.apache.wicket.MarkupContainer,
	 *      java.lang.String, org.apache.wicket.model.IModel)
	 */
	@Override
	protected FormComponent<T> newEditor(MarkupContainer parent, String componentId, IModel<T> model)
	{
		IModel<List<? extends T>> choiceModel = new AbstractReadOnlyModel<List<? extends T>>()
		{

			private static final long serialVersionUID = 1L;

			@Override
			public List<? extends T> getObject()
			{
				return choices.getObject();
			}

		};
		DropDownChoice<T> editor = new DropDownChoice<T>(componentId, model, choiceModel, renderer)
		{
			private static final long serialVersionUID = 1L;

			@Override
			protected void onModelChanged()
			{
				AjaxEditableChoiceLabel.this.onModelChanged();
			}

			@Override
			protected void onModelChanging()
			{
				AjaxEditableChoiceLabel.this.onModelChanging();
			}

		};
		editor.setOutputMarkupId(true);
		editor.setVisible(false);
		editor.add(new EditorAjaxBehavior()
		{
			private static final long serialVersionUID = 1L;

			@Override
			protected void onComponentTag(ComponentTag tag)
			{
				super.onComponentTag(tag);
				final String saveCall = "{wicketAjaxGet('" + getCallbackUrl() +
					"&save=true&'+this.name+'='+wicketEncode(this.value)); return true;}";

				final String cancelCall = "{wicketAjaxGet('" + getCallbackUrl() +
					"&save=false'); return false;}";

				tag.put("onchange", saveCall);
			}
		});
		return editor;
	}

	@Override
	protected WebComponent newLabel(MarkupContainer parent, String componentId, IModel<T> model)
	{
		Label label = new Label(componentId, model)
		{
			private static final long serialVersionUID = 1L;

			@Override
			public IConverter getConverter(Class<?> type)
			{
				IConverter c = AjaxEditableChoiceLabel.this.getConverter(type);
				return c != null ? c : super.getConverter(type);
			}

			@SuppressWarnings("unchecked")
			@Override
			protected void onComponentTagBody(MarkupStream markupStream, ComponentTag openTag)
			{
				String displayValue = getDefaultModelObjectAsString();
				if (renderer != null)
				{
					Object displayObject = renderer.getDisplayValue((T)getDefaultModelObject());
					Class<?> objectClass = (displayObject == null ? null : displayObject.getClass());


					if (objectClass != null && objectClass != String.class)
					{
						final IConverter converter = Application.get()
							.getConverterLocator()
							.getConverter(objectClass);

						displayValue = converter.convertToString(displayObject, getLocale());
					}
				}

				if (Strings.isEmpty(displayValue))
				{
					replaceComponentTagBody(markupStream, openTag, defaultNullLabel());
				}
				else
				{
					replaceComponentTagBody(markupStream, openTag, displayValue);
				}
			}
		};
		label.setOutputMarkupId(true);
		label.add(new LabelAjaxBehavior(getLabelAjaxEvent()));
		return label;
	}

	@Override
	protected void onModelChanged()
	{
		super.onModelChanged();
	}

	@Override
	protected void onModelChanging()
	{
		super.onModelChanging();
	}
}
