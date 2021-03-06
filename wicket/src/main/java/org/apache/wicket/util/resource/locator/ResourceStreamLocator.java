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
package org.apache.wicket.util.resource.locator;

import java.net.URL;
import java.util.Locale;

import org.apache.wicket.Application;
import org.apache.wicket.util.file.IResourceFinder;
import org.apache.wicket.util.resource.IResourceStream;
import org.apache.wicket.util.resource.UrlResourceStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Locate Wicket resource.
 * <p>
 * Contains the logic to locate a resource based on a path, a style (see
 * {@link org.apache.wicket.Session}), a locale and an extension string. The full filename will be
 * built like: &lt;path&gt;_&lt;style&gt;_&lt;locale&gt;.&lt;extension&gt;.
 * <p>
 * Resource matches will be attempted in the following order:
 * <ol>
 * <li>1. &lt;path&gt;_&lt;style&gt;_&lt;locale&gt;.&lt;extension&gt;</li>
 * <li>2. &lt;path&gt;_&lt;locale&gt;.&lt;extension&gt;</li>
 * <li>3. &lt;path&gt;_&lt;style&gt;.&lt;extension&gt;</li>
 * <li>4. &lt;path&gt;.&lt;extension&gt;</li>
 * </ol>
 * <p>
 * Locales may contain a language, a country and a region or variant. Combinations of these
 * components will be attempted in the following order:
 * <ol>
 * <li>locale.toString() see javadoc for Locale for more details</li>
 * <li>&lt;language&gt;_&lt;country&gt;</li>
 * <li>&lt;language&gt;</li>
 * </ol>
 * 
 * @author Juergen Donnerstag
 * @author Jonathan Locke
 */
public class ResourceStreamLocator implements IResourceStreamLocator
{
	/** Logging */
	private static final Logger log = LoggerFactory.getLogger(ResourceStreamLocator.class);

	/** If null, the application registered finder will be used */
	private IResourceFinder finder;

	/**
	 * Constructor
	 */
	public ResourceStreamLocator()
	{
	}

	/**
	 * Constructor
	 * 
	 * @param finder
	 *            resource finder
	 */
	public ResourceStreamLocator(final IResourceFinder finder)
	{
		this.finder = finder;
	}

	/**
	 * 
	 * @see org.apache.wicket.util.resource.locator.IResourceStreamLocator#locate(java.lang.Class,
	 *      java.lang.String)
	 */
	public IResourceStream locate(final Class< ? > clazz, final String path)
	{
		// First try with the resource finder registered with the application
		// (allows for markup reloading)
		IResourceStream stream = locateByResourceFinder(clazz, path);
		if (stream != null)
		{
			return stream;
		}

		// Then search the resource on the classpath
		stream = locateByClassLoader(clazz, path);
		if (stream != null)
		{
			return stream;
		}

		return null;
	}

	/**
	 * 
	 * @see org.apache.wicket.util.resource.locator.IResourceStreamLocator#locate(java.lang.Class,
	 *      java.lang.String, java.lang.String, java.util.Locale, java.lang.String)
	 */
	public IResourceStream locate(final Class< ? > clazz, String path, final String style,
		final Locale locale, final String extension)
	{
		// Try the various combinations of style, locale and extension to find
		// the resource.
		ResourceNameIterator iter = new ResourceNameIterator(path, style, locale, extension);
		while (iter.hasNext())
		{
			String newPath = (String)iter.next();

			IResourceStream stream = locate(clazz, newPath);
			if (stream != null)
			{
				stream.setLocale(iter.getLocale());
				return stream;
			}
		}

		return null;
	}

	/**
	 * Search the the resource my means of the various classloaders available
	 * 
	 * @param clazz
	 * @param path
	 * @return resource stream
	 */
	protected IResourceStream locateByClassLoader(final Class< ? > clazz, final String path)
	{
		ClassLoader classLoader = null;
		if (clazz != null)
		{
			classLoader = clazz.getClassLoader();
		}

		if (classLoader == null)
		{
			// use context classloader when no specific classloader is set
			// (package resources for instance)
			classLoader = Thread.currentThread().getContextClassLoader();
		}

		if (classLoader == null)
		{
			// use Wicket classloader when no specific classloader is set
			classLoader = getClass().getClassLoader();
		}

		// Log attempt
		if (log.isDebugEnabled())
		{
			log.debug("Attempting to locate resource '" + path + "' using classloader " +
				classLoader);
		}

		// Try loading path using classloader
		final URL url = classLoader.getResource(path);
		if (url != null)
		{
			return new UrlResourceStream(url);
		}
		return null;
	}

	/**
	 * Search the resource by means of the application registered resource finder
	 * 
	 * @param clazz
	 * @param path
	 * @return resource stream
	 */
	protected IResourceStream locateByResourceFinder(final Class< ? > clazz, final String path)
	{
		if (finder == null)
		{
			finder = Application.get().getResourceSettings().getResourceFinder();
		}

		// Log attempt
		if (log.isDebugEnabled())
		{
			log.debug("Attempting to locate resource '" + path + "' on path " + finder);
		}

		// Try to find file resource on the path supplied
		return finder.find(clazz, path);
	}
}
