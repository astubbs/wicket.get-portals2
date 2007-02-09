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
package wicket;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import wicket.model.IModel;
import wicket.resource.loader.IStringResourceLoader;
import wicket.settings.IResourceSettings;
import wicket.util.string.AppendingStringBuffer;
import wicket.util.string.Strings;
import wicket.util.string.interpolator.PropertyVariableInterpolator;

/**
 * A utility class that encapsulates all of the localization related
 * functionality in a way that it can be accessed by all areas of the framework
 * in a consistent way. A singleton instance of this class is available via the
 * <code>Application</code> object.
 * <p>
 * You may register additional IStringResourceLoader to extend or replace
 * Wickets default search strategy for the properties. E.g. string resource
 * loaders which load the properties from a database. There should be no need to
 * extend Localizer.
 * 
 * @see wicket.settings.Settings#getLocalizer()
 * @see wicket.resource.loader.IStringResourceLoader
 * @see wicket.settings.Settings#getStringResourceLoaders()
 * 
 * @author Chris Turner
 * @author Juergen Donnerstag
 */
public class Localizer
{
	/** Log */
	@SuppressWarnings("unused")
	private static final Logger log = LoggerFactory.getLogger(Localizer.class);

	/**
	 * Create the utils instance class backed by the configuration information
	 * contained within the supplied application object.
	 */
	public Localizer()
	{
	}

	/**
	 * @see #getString(String, Component, IModel, Locale, String, String)
	 * 
	 * @param key
	 *            The key to obtain the resource for
	 * @param component
	 *            The component to get the resource for
	 * @return The string resource
	 * @throws MissingResourceException
	 *             If resource not found and configuration dictates that
	 *             exception should be thrown
	 */
	public String getString(final String key, final Component component)
			throws MissingResourceException
	{
		return getString(key, component, null, null, null, null);
	}

	/**
	 * @see #getString(String, Component, IModel, Locale, String, String)
	 * 
	 * @param key
	 *            The key to obtain the resource for
	 * @param component
	 *            The component to get the resource for
	 * @param model
	 *            The model to use for property substitutions in the strings
	 *            (optional)
	 * @return The string resource
	 * @throws MissingResourceException
	 *             If resource not found and configuration dictates that
	 *             exception should be thrown
	 */
	public String getString(final String key, final Component component, final IModel model)
			throws MissingResourceException
	{
		return getString(key, component, model, null, null, null);
	}

	/**
	 * @see #getString(String, Component, IModel, Locale, String, String)
	 * 
	 * @param key
	 *            The key to obtain the resource for
	 * @param component
	 *            The component to get the resource for
	 * @param model
	 *            The model to use for property substitutions in the strings
	 *            (optional)
	 * @param defaultValue
	 *            The default value (optional)
	 * @return The string resource
	 * @throws MissingResourceException
	 *             If resource not found and configuration dictates that
	 *             exception should be thrown
	 */
	public String getString(final String key, final Component component, final IModel model,
			final String defaultValue) throws MissingResourceException
	{
		return getString(key, component, model, null, null, defaultValue);
	}

	/**
	 * @see #getString(String, Component, IModel, Locale, String, String)
	 * 
	 * @param key
	 *            The key to obtain the resource for
	 * @param component
	 *            The component to get the resource for
	 * @param defaultValue
	 *            The default value (optional)
	 * @return The string resource
	 * @throws MissingResourceException
	 *             If resource not found and configuration dictates that
	 *             exception should be thrown
	 */
	public String getString(final String key, final Component component, final String defaultValue)
			throws MissingResourceException
	{
		return getString(key, component, null, null, null, defaultValue);
	}

	/**
	 * Get the localized string using all of the supplied parameters. This
	 * method is left public to allow developers full control over string
	 * resource loading. However, it is recommended that one of the other
	 * convenience methods in the class are used as they handle all of the work
	 * related to obtaining the current user locale and style information.
	 * 
	 * @param key
	 *            The key to obtain the resource for
	 * @param component
	 *            The component to get the resource for (optional)
	 * @param model
	 *            The model to use for substitutions in the strings (optional)
	 * @param locale
	 *            The locale to get the resource for (optional)
	 * @param style
	 *            The style to get the resource for (optional) (see
	 *            {@link wicket.Session})
	 * @param defaultValue
	 *            The default value (optional)
	 * @return The string resource
	 * @throws MissingResourceException
	 *             If resource not found and configuration dictates that
	 *             exception should be thrown
	 */
	public String getString(final String key, final Component component, final IModel model,
			Locale locale, String style, final String defaultValue) throws MissingResourceException
	{
		final List searchStack;
		final String path;
		if (component != null)
		{
			// The reason why we need to create that stack is because we need to
			// walk it downwards starting with Page down to the Component
			searchStack = getComponentStack(component);
			path = Strings.replaceAll(component.getPageRelativePath(), ":", ".").toString();

			if (locale == null)
			{
				locale = component.getLocale();
			}

			if (style == null)
			{
				style = component.getStyle();
			}
		}
		else
		{
			searchStack = null;
			path = null;

			Session session = Session.get();
			if (locale == null)
			{
				locale = session.getLocale();
			}

			if (style == null)
			{
				style = session.getStyle();
			}
		}

		// Iterate over all registered string resource loaders until the
		// property has been found
		String string = visitResourceLoaders(key, path, searchStack, locale, style);

		// If a property value has been found, than replace the placeholder
		// and we are done
		if (string != null)
		{
			return substitutePropertyExpressions(component, string, model);
		}

		// Resource not found, so handle missing resources based on application
		// configuration
		final IResourceSettings resourceSettings = Application.get().getResourceSettings();
		if (resourceSettings.getUseDefaultOnMissingResource() && (defaultValue != null))
		{
			return defaultValue;
		}

		if (resourceSettings.getThrowExceptionOnMissingResource())
		{
			AppendingStringBuffer message = new AppendingStringBuffer("Unable to find resource: "
					+ key);
			if (component != null)
			{
				message.append(" for component: ");
				message.append(component.getPageRelativePath());
				message.append(" [class=").append(component.getClass().getName()).append("]");
			}
			throw new MissingResourceException(message.toString(), (component != null ? component
					.getClass().getName() : ""), key);
		}
		else
		{
			return "[Warning: String resource for '" + key + "' not found]";
		}
	}

	/**
	 * Note: This implementation does NOT perform variable substitution
	 * 
	 * @param key
	 *            The key to obtain the resource for
	 * @param searchStack
	 *            A stack of classes to get the resource for
	 * @param path
	 *            A (file) path to the resource containing the key
	 * @param locale
	 *            The locale to get the resource for (optional)
	 * @param style
	 *            The style to get the resource for (optional) (see
	 *            {@link wicket.Session})
	 * @return The string resource
	 * @throws MissingResourceException
	 *             If resource not found and configuration dictates that
	 *             exception should be thrown
	 */
	private String getString(final String key, final String path, final Locale locale,
			final String style) throws MissingResourceException
	{
		return visitResourceLoaders(key, path, null, locale, style);
	}

	/**
	 * Traverse the component hierachy up to the Page and add each component
	 * class to the list (stack) returned
	 * 
	 * @param component
	 *            The component to evaluate
	 * @return The stack of classes
	 */
	public static List<Class> getComponentStack(final Component component)
	{
		// No component, no stack
		if (component == null)
		{
			return null;
		}

		// Build the search stack
		final List<Class> searchStack = new ArrayList<Class>();
		searchStack.add(component.getClass());

		if (!(component instanceof Page))
		{
			// Add all the component on the way to the Page
			MarkupContainer container = component.getParent();
			while (container != null)
			{
				searchStack.add(container.getClass());
				if (container instanceof Page)
				{
					break;
				}

				container = container.getParent();
			}
		}
		return searchStack;
	}

	/**
	 * Helper method to handle preoprty variable substituion in strings.
	 * 
	 * @param component
	 *            The component requesting a model value
	 * @param string
	 *            The string to substitute into
	 * @param model
	 *            The model
	 * @return The resulting string
	 */
	private String substitutePropertyExpressions(final Component component, final String string,
			final IModel model)
	{
		if ((string != null) && (model != null))
		{
			return PropertyVariableInterpolator.interpolate(string, model.getObject());
		}
		return string;
	}

	/**
	 * For each StringResourceLoader registered with the application, load the
	 * properties file associated with the classes in the searchStack, the
	 * locale and the style. The searchStack is traversed in reverse order.
	 * <p>
	 * The property is identified by the 'key' or 'path'+'key'. 'path' is
	 * shortened (last element removed) to always represent the page relative
	 * path of the original component associate with it.
	 * 
	 * @param key
	 *            The key to obtain the resource for
	 * @param path
	 *            The component id path relative to the page
	 * @param searchStack
	 *            A stack of classes to get the resource for
	 * @param locale
	 *            The locale to get the resource for (optional)
	 * @param style
	 *            The style to get the resource for (optional) (see
	 *            {@link wicket.Session})
	 * @return The string resource
	 */
	private String visitResourceLoaders(final String key, final String path,
			final List searchStack, final Locale locale, final String style)
	{
		// Search each loader in turn and return the string if it is found
		final Iterator<IStringResourceLoader> iterator = Application.get().getResourceSettings()
				.getStringResourceLoaders().iterator();

		// The return value
		String string = null;

		// Iterate until a property has been found
		while (iterator.hasNext() && (string == null))
		{
			IStringResourceLoader loader = iterator.next();

			// The key prefix is equal to the component path relativ to the
			// current component on the top of the stack.
			String prefix = path;
			if ((searchStack != null) && (searchStack.size() > 0))
			{
				// Walk the component hierarchy down from page to the component
				for (int i = searchStack.size() - 1; (i >= 0) && (string == null); i--)
				{
					Class clazz = (Class)searchStack.get(i);

					// First check if a property with the 'key' provided by the
					// user is available.
					string = loader.loadStringResource(clazz, key, locale, style);

					// If not, prepend the component relativ path to the key
					if ((string == null) && (path != null) && (prefix.length() > 0))
					{
						string = loader
								.loadStringResource(clazz, prefix + '.' + key, locale, style);

						// If still not found, adjust the component relativ path
						// for the next component on the path from the page
						// down.
						if (string == null)
						{
							prefix = Strings.afterFirst(prefix, '.');
						}
					}
				}
			}
			else
			{
				// A default string resource loader, e.g. the
				// ApplicationStringResourceLoader,
				// does not necessarily require the component hierachy
				string = loader.loadStringResource(null, key, locale, style);
				if ((string == null) && (prefix != null) && (prefix.length() > 0))
				{
					string = loader.loadStringResource(null, prefix + '.' + key, locale, style);
				}
			}
		}

		return string;
	}
}