/*
 * $Id$
 * $Revision$ $Date$
 * 
 * ==============================================================================
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package wicket.markup.html.form.validation;

import java.util.HashMap;
import java.util.Map;
import java.util.MissingResourceException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import wicket.Localizer;
import wicket.markup.html.form.FormComponent;
import wicket.model.IModel;
import wicket.model.Model;
import wicket.util.lang.Classes;

/**
 * Base class for form component validators. This class is thread-safe and
 * therefore it is safe to share validators across sessions/threads.
 * <p>
 * Error messages can be registered on a component by calling one of the
 * error(FormComponent ...) overloads. The error message will be retrieved using
 * the Localizer for the form component. Normally, this localizer will find the
 * error message in a string resource bundle (properties file) associated with
 * the page in which this validator is contained. The key that is used to get
 * the message defaults to the pattern:
 * <code>[form-name].[component-name].[validator-class]</code>. For example:
 * <p>
 * MyForm.name.RequiredValidator=A name is required.
 * <p>
 * Error message string resources can contain optional property variable
 * interpolations from the component, such as:
 * <p>
 * editBook.name.LengthValidator='${input}' is too short a name.
 * <p>
 * Available variables for interpolation are:
 * <ul>
 * <li>${input} - The user's input</li>
 * <li>${name} - The name of the component</li>
 * <li>${label} - the label of the component - either comes from
 * FormComponent.labelModel or resource key [form-id].[form-component-id] in
 * that order</li>
 * </ul>
 * but specific validator subclasses may add more values.
 * </p>
 * 
 * @author Jonathan Locke
 * @author Eelco Hillenius
 */
public abstract class AbstractValidator implements IValidator
{
	/** Log. */
	private static Log log = LogFactory.getLog(AbstractValidator.class);

	/**
	 * Sets an error on the component being validated using the map returned by
	 * messageModel() for variable interpolations.
	 * <p>
	 * See class comments for details about how error messages are loaded and
	 * formatted.
	 * 
	 * @param formComponent
	 *            form component
	 */
	public void error(final FormComponent formComponent)
	{
		error(formComponent, messageModel(formComponent));
	}

	/**
	 * Returns a formatted validation error message for a given component. The
	 * error message is retrieved from a message bundle associated with the page
	 * in which this validator is contained using the given resource key. The
	 * resourceModel is used for variable interpolation.
	 * 
	 * @param formComponent
	 *            form component
	 * @param resourceKey
	 *            The resource key to use
	 * @param resourceModel
	 *            The model for variable interpolation
	 */
	public void error(final FormComponent formComponent, final String resourceKey,
			final IModel resourceModel)
	{
		// Return formatted error message
		Localizer localizer = formComponent.getLocalizer();

		// TODO Post 1.2: I didn't find a simpler way to getString() throw a
		// MissingResourceException without changes the application settings. I
		// guess this is something to change in 1.2 or 1.3
		String message = localizer.getString(resourceKey, formComponent.getParent(), resourceModel,
				"");
		if ((message == null) || (message.length() == 0))
		{
			if (formComponent.getApplication().getResourceSettings()
					.getThrowExceptionOnMissingResource())
			{
				throw new MissingResourceException("Unable to find resource: " + resourceKey,
						formComponent.getClass().getName(), resourceKey);
			}
			else
			// FIXME General: Quick fix for now to get settings working again
			{
				message = "[Warning: String resource for '" + resourceKey + "' not found]";
			}
		}
		formComponent.error(message);
	}

	/**
	 * Sets an error on the component being validated using the given map for
	 * variable interpolations.
	 * 
	 * @param formComponent
	 *            form component
	 * @param resourceKey
	 *            The resource key to use
	 * @param map
	 *            The model for variable interpolation
	 */
	public void error(final FormComponent formComponent, final String resourceKey, final Map map)
	{
		error(formComponent, resourceKey, Model.valueOf(map));
	}

	/**
	 * Sets an error on the component being validated using the given map for
	 * variable interpolations.
	 * 
	 * @param formComponent
	 *            form component
	 * @param map
	 *            The model for variable interpolation
	 */
	public void error(final FormComponent formComponent, final Map map)
	{
		IModel model = Model.valueOf(map);
		try
		{
			error(formComponent, resourceKey(formComponent), model);
		}
		catch (MissingResourceException ex)
		{
			String key = Classes.name(getClass());
			error(formComponent, key, model);
		}
	}

	/**
	 * Gets the resource key for validator's error message from the
	 * ApplicationSettings class.
	 * 
	 * @param formComponent
	 *            form component that is being validated
	 * 
	 * @return the resource key based on the form component
	 */
	protected String resourceKey(final FormComponent formComponent)
	{
		return formComponent.getApplication().getResourceSettings()
				.getValidatorResourceKeyFactory().newKey(this, formComponent);
	}

	/**
	 * Gets the default variables for interpolation. These are:
	 * <ul>
	 * <li>${input}: the user's input</li>
	 * <li>${name}: the name of the component</li>
	 * <li>${label}: the label of the component - either comes from
	 * FormComponent.labelModel or resource key [form-id].[form-component-id] in
	 * that order</li>
	 * </ul>
	 * 
	 * @param formComponent
	 *            form component
	 * @return a map with the variables for interpolation
	 */
	protected Map messageModel(final FormComponent formComponent)
	{
		final Map resourceModel = new HashMap(4);
		resourceModel.put("input", formComponent.getInput());
		resourceModel.put("name", formComponent.getId());

		Object label = null;
		if (formComponent.getLabel() != null)
		{
			label = formComponent.getLabel().getObject(formComponent);
		}

		if (label != null)
		{
			resourceModel.put("label", label);
		}
		else
		{
			// apply default value (component id) if key/value can not be found
			resourceModel.put("label", formComponent.getLocalizer().getString(
					formComponent.getId(), formComponent.getParent(), formComponent.getId()));
		}
		return resourceModel;
	}
}
