/*
 * $Id$
 * $Revision$
 * $Date$
 *
 * ==============================================================================
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package wicket.markup;

import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import wicket.Application;
import wicket.MarkupContainer;
import wicket.WicketRuntimeException;
import wicket.util.listener.IChangeListener;
import wicket.util.resource.IResourceStream;
import wicket.util.resource.ResourceStreamNotFoundException;
import wicket.util.watch.ModificationWatcher;

/**
 * Load markup and cache it for fast retrieval. If markup file changes, it'll
 * be automatically reloaded.
 * 
 * @author Jonathan Locke
 * @author Juergen Donnerstag
 */
public class MarkupCache
{
	/** Log for reporting. */
	private static final Log log = LogFactory.getLog(MarkupCache.class);

	/** Map of markup tags by class. */
	private static final Map markupCache = new HashMap();

	/** the Wicket application */
	private final Application application;

	/**
	 * Constructor.
	 * 
	 * @param application
	 */
	public MarkupCache(final Application application)
	{
		this.application = application;
	}

	/**
	 * Gets a fresh markup stream that contains the (immutable) markup resource
	 * for this class.
	 * 
	 * @param container
	 *            The container the markup should be associated with
	 * @param clazz
	 *            The class to get the associated markup for. If null, the the
	 *            container's class is used, but it can be a parent class of
	 *            container as well.
	 * 
	 * @return A stream of MarkupElement elements
	 */
	public final MarkupStream getMarkupStream(final MarkupContainer container, final Class clazz)
	{
		// Look for associated markup
		final Markup markup = getMarkup(container, clazz);

		// If we found markup for this container
		if (markup != Markup.NO_MARKUP)
		{
			// return a MarkupStream for the markup
			return new MarkupStream(markup);
		}
		else
		{
			// throw exception since there is no associated markup
			throw new WicketRuntimeException(
					"Markup not found. Component class: "
							+ clazz.getName()
							+ " Enable debug messages for wicket.util.resource.Resource to get a list of all filenames tried.");
		}
	}

	/**
	 * @param container
	 * @param clazz
	 * @return True if this markup container has associated markup
	 */
	public final boolean hasAssociatedMarkup(final MarkupContainer container, final Class clazz)
	{
		return getMarkup(container, clazz) != Markup.NO_MARKUP;
	}

	/**
	 * Gets any (immutable) markup resource for this class.
	 * 
	 * @param container
	 *            The container the markup should be associated with
	 * @param clazz
	 *            The class to get the associated markup for. If null, the the
	 *            container's class is used, but it can be a parent class of
	 *            container as well.
	 * @return Markup resource
	 */
	// This is class is already prepared for markup inheritance
	private final Markup getMarkup(final MarkupContainer container, Class clazz)
	{
		if (clazz == null)
		{
			clazz = container.getClass();
		}
		else
		{
			if (!clazz.isInstance(container))
			{
				throw new WicketRuntimeException("Parameter clazz must be instance of container");
			}
		}

		synchronized (markupCache)
		{
			// Look up markup tag list by class, locale, style and markup type
			final String key = markupKey(container, clazz);
			Markup markup = (Markup)markupCache.get(key);

			// If no markup in map
			if (markup == null)
			{
				// Locate markup resource, searching up class hierarchy
				IResourceStream markupResource = null;
				Class containerClass = clazz;

				while ((markupResource == null) && (containerClass != MarkupContainer.class))
				{
					// Look for markup resource for containerClass
					markupResource = application.getResourceStreamLocator().locate(containerClass,
							container.getStyle(), container.getLocale(), container.getMarkupType());
					containerClass = containerClass.getSuperclass();
				}

				// Found markup?
				if (markupResource != null)
				{
					// load the markup and watch for changes
					markup = loadMarkupAndWatchForChanges(key, markupResource);
				}
				else
				{
					// flag markup as non-existent (as opposed to null, which
					// might mean that it's simply not loaded into the cache)
					markup = Markup.NO_MARKUP;
				}

				// Save any markup list (or absence of one) for next time
				markupCache.put(key, markup);
			}

			return markup;
		}
	}

	/**
	 * Loads markup.
	 * 
	 * @param key
	 *            Key under which markup should be cached
	 * @param markupResource
	 *            The markup resource to load
	 * @return The markup
	 * @throws ParseException
	 * @throws IOException
	 * @throws ResourceStreamNotFoundException
	 */
	private Markup loadMarkup(final String key, final IResourceStream markupResource)
			throws ParseException, IOException, ResourceStreamNotFoundException
	{
		final Markup markup = application.getMarkupParser().readAndParse(markupResource);
		markupCache.put(key, markup);
		return markup;
	}

	/**
	 * Load markup and add a {@link ModificationWatcher}to the markup resource.
	 * 
	 * @param key
	 *            The key for the resource
	 * @param markupResource
	 *            The markup file to load and begin to watch
	 * @return The markup in the file
	 */
	private Markup loadMarkupAndWatchForChanges(final String key, final IResourceStream markupResource)
	{
		try
		{
			// Watch file in the future
			final ModificationWatcher watcher = application.getResourceWatcher();

			if (watcher != null)
			{
				watcher.add(markupResource, new IChangeListener()
				{
					public void onChange()
					{
						synchronized (markupCache)
						{
							try
							{
								log.info("Reloading markup from " + markupResource);
								loadMarkup(key, markupResource);
							}
							catch (ParseException e)
							{
								log.error("Unable to parse markup from " + markupResource, e);
							}
							catch (ResourceStreamNotFoundException e)
							{
								log.error("Unable to find markup from " + markupResource, e);
							}
							catch (IOException e)
							{
								log.error("Unable to read markup from " + markupResource, e);
							}
						}
					}
				});
			}

			log.info("Loading markup from " + markupResource);

			return loadMarkup(key, markupResource);
		}
		catch (ParseException e)
		{
			throwException(e, markupResource, "Unable to parse markup from ");
		}
		catch (MarkupException e)
		{
			throwException(e, markupResource, e.getMessage());
		}
		catch (ResourceStreamNotFoundException e)
		{
			throwException(e, markupResource, "Unable to find markup from ");
		}
		catch (IOException e)
		{
			throwException(e, markupResource, "Unable to read markup from ");
		}

		return Markup.NO_MARKUP;
	}

	/**
	 * @param container
	 * @param clazz
	 *            The clazz to get the key for
	 * @return Key that uniquely identifies any markup that might be associated
	 *         with this markup container.
	 */
	private String markupKey(final MarkupContainer container, final Class clazz)
	{
		return clazz.getName() + container.getLocale() + container.getStyle()
				+ container.getMarkupType();
	}

	/**
	 * 
	 * @param e
	 * @param resource
	 * @param message
	 * @throws MarkupException
	 */
	private void throwException(final Exception e, final IResourceStream resource, final String message)
			throws MarkupException
	{
		throw new MarkupException(resource, message + resource, e);
	}
}
