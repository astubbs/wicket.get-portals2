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
package org.apache.wicket.extensions.yui.calendar;

import java.util.Date;

import org.apache.wicket.datetime.markup.html.form.DateTextField;
import org.apache.wicket.markup.html.form.FormComponentPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.joda.time.MutableDateTime;

/**
 * Works on a {@link java.util.Date} object. Displays a {@link DateTextField} and a
 * {@link DatePicker calendar popup}.<br/>
 * <p>
 * Note: {@link DateField} must <strong>not</strong> be associated with an
 * <code>&lt;input&gt;</code> tag, as opposed to {@link DateTextField}! The corresponding tag is
 * typically either a <code>&lt;div&gt;</code> or a <code>&lt;span&gt;</code> tag.
 * </p>
 * 
 * Example:
 * <p>
 * <u>Java:</u>
 * 
 * <pre>
 * DateField dateField = new DateField(&quot;birthday&quot;);
 * </pre>
 * 
 * </p>
 * <p>
 * <u>Markup:</u>
 * 
 * <pre>
 * &lt;div wicket:id=&quot;birthday&quot;&gt;&lt;/div&gt;
 * </pre>
 * 
 * </p>
 * 
 * 
 * 
 * @author eelcohillenius
 */
public class DateField extends FormComponentPanel<Date>
{
	private static final long serialVersionUID = 1L;

	private MutableDateTime date;

	private final DateTextField dateField;

	/**
	 * Construct.
	 * 
	 * @param id
	 */
	public DateField(String id)
	{
		this(id, null);
	}

	/**
	 * Construct.
	 * 
	 * @param id
	 * @param model
	 */
	public DateField(String id, IModel<Date> model)
	{
		super(id, model);
		setType(Date.class);
		PropertyModel<Date> dateFieldModel = new PropertyModel<Date>(this, "date");
		add(dateField = newDateTextField("date", dateFieldModel));
		dateField.add(new DatePicker());
	}

	/**
	 * Gets date.
	 * 
	 * @return date
	 */
	public Date getDate()
	{
		return (date != null) ? date.toDate() : null;
	}

	/**
	 * @see org.apache.wicket.markup.html.form.FormComponent#getInput()
	 */
	@Override
	public String getInput()
	{
		// since we override convertInput, we can let this method return a value
		// that is just suitable for error reporting
		return dateField.getInput();
	}

	/**
	 * Sets date.
	 * 
	 * @param date
	 *            date
	 */
	public void setDate(Date date)
	{
		this.date = (date != null) ? new MutableDateTime(date) : null;
		setDefaultModelObject(date);
	}

	/**
	 * Sets the converted input. In this case, we're really just interested in the nested date
	 * field, as that is the element that receives the real user input. So we're just passing that
	 * on.
	 * <p>
	 * Note that overriding this method is a better option than overriding {@link #updateModel()}
	 * like the first versions of this class did. The reason for that is that this method can be
	 * used by form validators without having to depend on the actual model being updated, and this
	 * method is called by the default implementation of {@link #updateModel()} anyway (so we don't
	 * have to override that anymore).
	 * </p>
	 * 
	 * @return instance of {@link Date}, possibly null
	 * 
	 * @see org.apache.wicket.markup.html.form.FormComponent#convertInput()
	 */
	@Override
	protected void convertInput()
	{
		setConvertedInput(dateField.getConvertedInput());
	}

	/**
	 * @deprecated replaced by {@link #newDateTextField(String, PropertyModel)}
	 */
	// TODO remove after deprecation release
	@Deprecated
	protected final DateTextField newDateTextField(PropertyModel dateFieldModel)
	{
		throw new UnsupportedOperationException();
	}

	/**
	 * create a new {@link DateTextField} instance to be added to this panel.
	 * 
	 * @param id
	 *            The id to be used when creating the component
	 * @param dateFieldModel
	 *            model that should be used by the {@link DateTextField}
	 * @return a new date text field instance
	 */
	protected DateTextField newDateTextField(String id, PropertyModel dateFieldModel)
	{
		return DateTextField.forShortStyle(id, dateFieldModel);
	}

	/**
	 * @see org.apache.wicket.Component#onBeforeRender()
	 */
	@Override
	protected void onBeforeRender()
	{
		dateField.setRequired(isRequired());
		dateField.setEnabled(isEnabled() && isEnableAllowed());

		Date d = (Date)getDefaultModelObject();
		if (d != null)
		{
			date = new MutableDateTime(d);
		}
		else
		{
			date = null;
		}

		super.onBeforeRender();
	}
}
