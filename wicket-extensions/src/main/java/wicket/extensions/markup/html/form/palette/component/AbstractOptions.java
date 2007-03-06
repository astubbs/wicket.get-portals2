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
package wicket.extensions.markup.html.form.palette.component;

import java.util.Iterator;

import wicket.extensions.markup.html.form.palette.Palette;
import wicket.markup.ComponentTag;
import wicket.markup.MarkupStream;
import wicket.markup.html.form.FormComponent;
import wicket.markup.html.form.IChoiceRenderer;
import wicket.util.string.AppendingStringBuffer;
import wicket.util.value.ValueMap;

/**
 * Generats html option elements based on iterator specified by
 * getOptionsIterator() and IChoiceRender specified by the palette
 * 
 * @author Igor Vaynberg ( ivaynberg )
 * 
 */
public abstract class AbstractOptions extends FormComponent
{
	private Palette palette;

	protected Palette getPalette()
	{
		return palette;
	}

	/**
	 * @param id
	 *            component id
	 * @param palette
	 *            parent palette
	 */
	public AbstractOptions(String id, Palette palette)
	{
		super(id);
		this.palette = palette;
		setOutputMarkupId(true);
	}

	protected abstract Iterator getOptionsIterator();

		
	protected void onComponentTagBody(MarkupStream markupStream, ComponentTag openTag)
	{
		final AppendingStringBuffer buffer = new AppendingStringBuffer(128);
		Iterator options = getOptionsIterator();
		IChoiceRenderer renderer = getPalette().getChoiceRenderer();

		while (options.hasNext())
		{
			final Object choice = options.next();
			String id = renderer.getIdValue(choice, 0);
			Object displayValue = renderer.getDisplayValue(choice);
			Class displayClass = displayValue == null ? null : displayValue.getClass();
			String value = getConverter(displayClass).convertToString(displayValue, getLocale());
			value = getLocalizer().getString(id + "." + value, this, value);

			buffer.append("\n<option value=\"").append(id).append("\">").append(value).append(
					"</option>");

		}

		buffer.append("\n");
		replaceComponentTagBody(markupStream, openTag, buffer);
	}

	protected void onComponentTag(ComponentTag tag)
	{
		checkComponentTag(tag, "select");

		super.onComponentTag(tag);
		ValueMap attrs = tag.getAttributes();

		attrs.put("multiple", null);
		attrs.put("size", new Integer(getPalette().getRows()));

		if (!palette.isPaletteEnabled()) {
			attrs.put("disabled","disabled");
		}

	}

	/**
	 * @see wicket.markup.html.form.FormComponent#updateModel()
	 */
	public void updateModel()
	{
	}

}
