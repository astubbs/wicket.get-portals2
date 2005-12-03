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
package wicket;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import wicket.authorization.IAuthorizationStrategy;
import wicket.markup.MarkupCache;
import wicket.markup.MarkupParser;
import wicket.markup.html.BodyOnLoadResolver;
import wicket.markup.html.HtmlHeaderResolver;
import wicket.markup.html.WicketLinkResolver;
import wicket.markup.html.WicketMessageResolver;
import wicket.markup.html.image.resource.DefaultButtonImageResourceFactory;
import wicket.markup.parser.XmlPullParser;
import wicket.model.IModel;
import wicket.request.IRequestCycleProcessor;
import wicket.resource.PropertiesFactory;
import wicket.util.convert.ConverterFactory;
import wicket.util.convert.IConverterFactory;
import wicket.util.crypt.ICrypt;
import wicket.util.crypt.NoCrypt;
import wicket.util.lang.Classes;
import wicket.util.resource.locator.DefaultResourceStreamLocator;
import wicket.util.resource.locator.ResourceStreamLocator;
import wicket.util.string.Strings;
import wicket.util.time.Duration;
import wicket.util.watch.ModificationWatcher;

/**
 * Base class for all Wicket applications. To create a Wicket application, you
 * generally should <i>not </i> directly subclass this class. Instead, you will
 * want to subclass some subclass of Application, like WebApplication, which is
 * appropriate for the protocol and markup type you are working with.
 * 
 * Application has the following interesting features / attributes:
 * <ul>
 * <li><b>Name </b>- The application's name, which is the same as its class
 * name.
 * 
 * <li><b>Application Settings </b>- A variety of settings that control the
 * behavior of the Wicket framework for a given application. It is not necessary
 * to learn all the settings. Good defaults can be set for deployment and
 * development by calling ApplicationSettings.configure("deployment") or
 * ApplicationSettings.configure("development").
 * 
 * <li><b>Application Pages </b>- A particular set of required pages. The
 * required pages returned by getPages() include a home page and pages for
 * handling common error conditions. The only page you must supply to create a
 * Wicket application is the application home page.
 * 
 * <li><b>Shared Resources </b>- Resources added to an application with any of
 * the Application.addResource() methods have application-wide scope and can be
 * referenced using a logical scope and a name with the ResourceReference class.
 * resourceReferences can then be used by multiple components in the same
 * application without additional overhead (beyond the ResourceReference
 * instance held by each referee) and will yield a stable URL, permitting
 * efficient browser caching of the resource (even if the resource is
 * dynamically generated). Resources shared in this manner may also be
 * localized. See {@link wicket.ResourceReference} for more details.
 * 
 * <li><b>A Converter Factory </b>- By overriding getConverterFactory(), you
 * can provide your own factory which creates locale sensitive Converter
 * instances.
 * 
 * <li><b>A ResourceStreamLocator </b>- An Application's ResourceStreamLocator
 * is used to find resources such as images or markup files. You can supply your
 * own ResourceStreamLocator if your prefer to store your application's
 * resources in a non-standard location (such as a different filesystem
 * location, a particular JAR file or even a database) by overriding the
 * getResourceLocator() method.
 * 
 * <li><b>Resource Factories </b>- Resource factories can be used to create
 * resources dynamically from specially formatted HTML tag attribute values. For
 * more details, see {@link IResourceFactory},
 * {@link wicket.markup.html.image.resource.DefaultButtonImageResourceFactory}
 * and especially
 * {@link wicket.markup.html.image.resource.LocalizedImageResource}.
 * 
 * <li><b>A Localizer </b>- The getLocalizer() method returns an object
 * encapsulating all of the functionality required to access localized
 * resources. For many localization problems, even this will not be required, as
 * there are convenience methods available to all components:
 * {@link wicket.Component#getString(String key)} and
 * {@link wicket.Component#getString(String key, IModel model)}.
 * 
 * <li><b>A Session Factory </b>- The Application subclass WebApplication
 * supplies an implementation of getSessionFactory() which returns an
 * implementation of ISessionFactory that creates WebSession Session objects
 * appropriate for web applications. You can (and probably will want to)
 * override getSessionFactory() to provide your own session factory that creates
 * Session instances of your own application-specific subclass of WebSession.
 * 
 * </ul>
 * 
 * @see wicket.protocol.http.WebApplication
 * @author Jonathan Locke
 */
public abstract class Application
{
	/** log. */
	private static Log log = LogFactory.getLog(Application.class);

	/** List of (static) ComponentResolvers */
	private List componentResolvers = new ArrayList();

	/**
	 * Factory for the converter instance; default to the non localized factory
	 * {@link ConverterFactory}.
	 */
	private IConverterFactory converterFactory = new ConverterFactory();

	/** the authorization strategy. */
	private IAuthorizationStrategy authorizationStrategy = IAuthorizationStrategy.ALLOW_ALL;

	/** The single application-wide localization class */
	private Localizer localizer;

	/** Markup cache for this application */
	private final MarkupCache markupCache;

	/** Name of application subclass. */
	private final String name;

	/** Map to look up resource factories by name */
	private final Map nameToResourceFactory = new HashMap();

	/** Pages for application */
	private final ApplicationPages pages = new ApplicationPages();

	/** The default resource locator for this application */
	private ResourceStreamLocator resourceStreamLocator;

	/** ModificationWatcher to watch for changes in markup files */
	private ModificationWatcher resourceWatcher;

	/** Settings for application. */
	private ApplicationSettings settings;

	/** Shared resources for the application */
	private final SharedResources sharedResources;

	/** cached encryption/decryption object. */
	private ICrypt crypt;

	/**
	 * List of {@link IResponseFilter}s.
	 */
	// TODO revisit... I don't think everyone agrees with
	// this (e.g. why not use servlet filters to acchieve the same)
	// and if we want to support this, it could more elegantly
	// be made part of e.g. request targets or a request processing
	// strategy
	private List responseFilters;

	/** The factory to be used for the property files */
	private PropertiesFactory propertiesFactory;
	
	/** thread local holder of the application object. */
	private static final ThreadLocal CURRENT = new ThreadLocal();

	/**
	 * Get application for current session.
	 * 
	 * @return The current application
	 */
	public static Application get()
	{
		return (Application)CURRENT.get();
	}

	/**
	 * THIS METHOD IS NOT PART OF THE WICKET PUBLIC API. DO NOT USE IT.
	 * 
	 * @param application
	 *            The current application or null for this thread
	 */
	public static void set(Application application)
	{
		CURRENT.set(application);
	}

	/**
	 * Constructor
	 */
	public Application()
	{
		// Create name from subclass
		this.name = Classes.name(getClass());

		// Construct markup cache fot this application
		this.markupCache = new MarkupCache(this);

		// Create shared resources repository
		this.sharedResources = new SharedResources(this);

		// Install default component resolvers
		componentResolvers.add(new AutoComponentResolver());
		componentResolvers.add(new MarkupInheritanceResolver());
		componentResolvers.add(new HtmlHeaderResolver());
		componentResolvers.add(new BodyOnLoadResolver());
		componentResolvers.add(new WicketLinkResolver());
		componentResolvers.add(new WicketMessageResolver());

		// Install button image resource factory
		addResourceFactory("buttonFactory", new DefaultButtonImageResourceFactory());
	}

	/**
	 * Adds a resource factory to the list of factories to consult when
	 * generating resources automatically
	 * 
	 * @param name
	 *            The name to give to the factory
	 * @param resourceFactory
	 *            The resource factory to add
	 */
	public final void addResourceFactory(final String name, final IResourceFactory resourceFactory)
	{
		nameToResourceFactory.put(name, resourceFactory);
	}

	/**
	 * Get the (modifiable) list of IComponentResolvers.
	 * 
	 * @see AutoComponentResolver for an example
	 * @return List of ComponentResolvers
	 */
	public final List getComponentResolvers()
	{
		return componentResolvers;
	}

	/**
	 * Gets the converter factory.
	 * 
	 * @return the converter factory
	 */
	public IConverterFactory getConverterFactory()
	{
		return converterFactory;
	}

	/**
	 * Gets the authorization strategy.
	 * 
	 * @return Returns the authorizationStrategy.
	 */
	public IAuthorizationStrategy getAuthorizationStrategy()
	{
		return authorizationStrategy;
	}

	/**
	 * Get the application's localizer.
	 * <p>
	 * Note: please @see ApplicationSettings#addStringResourceLoader(IStringResourceLoader)
	 * for means of extending the way Wicket resolves keys to localized messages.
	 *   
	 * @return The application wide localizer instance
	 */
	public final Localizer getLocalizer()
	{
		if (localizer == null)
		{
			this.localizer = new Localizer(this);
		}
		return localizer;
	}

	/**
	 * Users may provide there own implementation of a localizer, e.g. one which
	 * uses Spring's MessageSource.
	 * 
	 * @param localizer
	 */
	public final void setLocalizer(final Localizer localizer)
	{
		this.localizer = localizer;
	}

	/**
	 * THIS METHOD IS NOT PART OF THE WICKET PUBLIC API. DO NOT USE IT.
	 * 
	 * @return Returns the markup cache associated with the application
	 */
	public final MarkupCache getMarkupCache()
	{
		return this.markupCache;
	}

	/**
	 * Gets the name of this application.
	 * 
	 * @return The application name.
	 */
	public final String getName()
	{
		return name;
	}

	/**
	 * @return Application's common pages
	 */
	public final ApplicationPages getPages()
	{
		return pages;
	}

	/**
	 * THIS FEATURE IS CURRENTLY EXPERIMENTAL. DO NOT USE THIS METHOD.
	 * 
	 * @param page
	 *            The Page for which a list of PageSets should be retrieved
	 * @return Sequence of PageSets for a given Page
	 */
	public final Iterator getPageSets(final Page page)
	{
		return new Iterator()
		{
			public boolean hasNext()
			{
				return false;
			}

			public Object next()
			{
				return null;
			}

			public void remove()
			{
			}
		};
	}

	/**
	 * @param name
	 *            Name of the factory to get
	 * @return The IResourceFactory with the given name.
	 */
	public final IResourceFactory getResourceFactory(final String name)
	{
		return (IResourceFactory)nameToResourceFactory.get(name);
	}

	/**
	 * @return Resource locator for this application
	 */
	public ResourceStreamLocator getResourceStreamLocator()
	{
		if (resourceStreamLocator == null)
		{
			// Create compound resource locator using source path from
			// application settings
			resourceStreamLocator = new DefaultResourceStreamLocator(getSettings()
					.getResourceFinder());
		}
		return resourceStreamLocator;
	}

	/**
	 * @return Resource watcher with polling frequency determined by setting, or
	 *         null if no polling frequency has been set.
	 */
	public final ModificationWatcher getResourceWatcher()
	{
		if (resourceWatcher == null)
		{
			final Duration pollFrequency = getSettings().getResourcePollFrequency();
			if (pollFrequency != null)
			{
				resourceWatcher = new ModificationWatcher(pollFrequency);
			}
		}
		return resourceWatcher;
	}

	/**
	 * @return Application settings
	 */
	public ApplicationSettings getSettings()
	{
		if (settings == null)
		{
			settings = createApplicationSettings();
		}
		return settings;
	}

	/**
	 * Subclasses could override this to give there own implementation of
	 * ApplicationSettings. DO NOT CALL THIS METHOD YOURSELF. Use getSettings
	 * instead.
	 * 
	 * @return An instanceof an ApplicationSettings class.
	 */
	public ApplicationSettings createApplicationSettings()
	{
		return new ApplicationSettings(this);
	}

	/**
	 * @return Returns the sharedResources.
	 */
	public final SharedResources getSharedResources()
	{
		return sharedResources;
	}

	/**
	 * Factory method that creates an instance of de-/encryption class. NOTE:
	 * this implementation caches the crypt instance, so it has to be
	 * Threadsafe. If you want other behaviour, or want to provide a custom
	 * crypt class, you should override this method.
	 * 
	 * @return Instance of de-/encryption class
	 */
	public synchronized ICrypt newCrypt()
	{
		if (crypt == null)
		{
			Class cryptClass = getSettings().getCryptClass();
			try
			{
				crypt = (ICrypt)cryptClass.newInstance();
				log.info("using encryption/decryption object " + crypt);
				crypt.setKey(getSettings().getEncryptionKey());
				return crypt;
			}
			catch (Throwable e)
			{
				log.warn("************************** WARNING **************************");
				log.warn("As the instantion of encryption/decryption class:");
				log.warn("\t" + cryptClass);
				log.warn("failed, Wicket will fallback on a dummy implementation");
				log.warn("\t(" + NoCrypt.class.getName() + ")");
				log.warn("This is not recommended for production systems.");
				log.warn("Please override method wicket.Application.newCrypt()");
				log.warn("to provide a custom encryption/decryption implementation");
				log.warn("The cause of the instantion failure: ");
				log.warn("\t" + e.getMessage());
				if (log.isDebugEnabled())
				{
					log.debug("exception: ", e);
				}
				else
				{
					log.warn("set log level to DEBUG to display the stack trace.");
				}
				log.warn("*************************************************************");

				// assign the dummy crypt implementation
				crypt = new NoCrypt();
			}
		}

		return crypt;
	}

	/**
	 * Factory method that creates a markup parser.
	 * 
	 * @param container
	 *            The wicket container requesting the markup
	 * @return A new MarkupParser
	 */
	public MarkupParser newMarkupParser(final MarkupContainer container)
	{
		final MarkupParser parser = new MarkupParser(container, new XmlPullParser(getSettings()
				.getDefaultMarkupEncoding()));

		parser.configure(getSettings());
		return parser;
	}

	/**
	 * @return Factory for creating sessions
	 */
	protected abstract ISessionFactory getSessionFactory();

	/**
	 * Gets the default request cycle processor (with lazy initialization). This
	 * is the {@link IRequestCycleProcessor} that will be used by
	 * {@link RequestCycle}s when custom implementations of the request cycle
	 * do not provide their own customized versions.
	 * 
	 * @return the default request cycle processor
	 */
	protected abstract IRequestCycleProcessor getDefaultRequestCycleProcessor();

	/**
	 * Adds a response filer to the list. Filters are evaluated in the order
	 * they have been added.
	 * 
	 * @param responseFilter
	 *            The {@link IResponseFilter} that is added
	 */
	public final void addResponseFilter(IResponseFilter responseFilter)
	{
		if (responseFilters == null)
		{
			responseFilters = new ArrayList(3);
		}
		responseFilters.add(responseFilter);
	}

	/**
	 * THIS METHOD IS NOT PART OF THE WICKET PUBLIC API. DO NOT USE IT.
	 * 
	 * Loops over all the response filters that were set (if any) with the give
	 * response returns the response buffer itself if there where now filters or
	 * the response buffer that was created/returned by the filter(s)
	 * 
	 * @param responseBuffer
	 *            The response buffer to be filtered
	 * @return Returns the filtered string buffer.
	 */
	public final StringBuffer filterResponse(StringBuffer responseBuffer)
	{
		if (responseFilters == null)
		{
			return responseBuffer;
		}
		for (int i = 0; i < responseFilters.size(); i++)
		{
			IResponseFilter filter = (IResponseFilter)responseFilters.get(i);
			responseBuffer = filter.filter(responseBuffer);
		}
		return responseBuffer;
	}

	/**
	 * Allows for initialization of the application by a subclass.
	 */
	protected void init()
	{
	}

	/**
	 * Template method that is called when a runtime exception is thrown, just
	 * before the actual handling of the runtime exception.
	 * 
	 * @param page
	 *            Any page context where the exception was thrown
	 * @param e
	 *            The exception
	 * @return Any error page to redirect to
	 */
	protected Page onRuntimeException(final Page page, final RuntimeException e)
	{
		return null;
	}

	/**
	 * THIS METHOD IS NOT PART OF THE WICKET PUBLIC API. DO NOT OVERRIDE OR
	 * CALL.
	 * 
	 * Internal intialization.
	 */
	protected void internalInit()
	{
		// We initialize components here rather than in the constructor because
		// the Application constructor is run before the Application subclass'
		// constructor and that subclass constructor may add class aliases that
		// would be used in installing resources in the component.
		initializeComponents();
	}

	/**
	 * Called by ApplicationSettings when source path property is changed. This
	 * method sets the resourceStreamLocator to null so it will get recreated
	 * the next time it is accessed using the new source path.
	 */
	final void resourceFinderChanged()
	{
		this.resourceStreamLocator = null;
	}

	/**
	 * Initializes wicket components
	 */
	private final void initializeComponents()
	{
		// Load any wicket components we can find
		try
		{
			// Load components used by all applications
			for (Enumeration e = getClass().getClassLoader().getResources("wicket.properties"); e
					.hasMoreElements();)
			{
				InputStream is = null;
				try
				{
					final URL url = (URL)e.nextElement();
					final Properties properties = new Properties();
					is = url.openStream();
					properties.load(is);
					initializeComponents(properties);
				}
				finally
				{
					if (is != null)
					{
						is.close();
					}
				}
			}
		}
		catch (IOException e)
		{
			throw new WicketRuntimeException("Unable to load initializers file", e);
		}
	}

	/**
	 * @param properties
	 *            Properties table with names of any library initializers in it
	 */
	private void initializeComponents(final Properties properties)
	{
		initialize(properties.getProperty("initializer"));
		initialize(properties.getProperty(getName() + "-initializer"));
	}

	/**
	 * Instantiate initializer with the given class name
	 * 
	 * @param className
	 *            The name of the initializer class
	 */
	private void initialize(final String className)
	{
		if (!Strings.isEmpty(className))
		{
			try
			{
				Class c = getClass().getClassLoader().loadClass(className);
				((IInitializer)c.newInstance()).init(this);
			}
			catch (ClassCastException e)
			{
				throw new WicketRuntimeException("Unable to initialize " + className, e);
			}
			catch (ClassNotFoundException e)
			{
				throw new WicketRuntimeException("Unable to initialize " + className, e);
			}
			catch (InstantiationException e)
			{
				throw new WicketRuntimeException("Unable to initialize " + className, e);
			}
			catch (IllegalAccessException e)
			{
				throw new WicketRuntimeException("Unable to initialize " + className, e);
			}
		}
	}
	
	/**
	 * Get the property factory which will be used to load properties files
	 * 
	 * @return PropertiesFactory
	 */
	public PropertiesFactory getPropertiesFactory()
	{
		if (propertiesFactory == null)
		{
			propertiesFactory = new PropertiesFactory();
		}
		return propertiesFactory;
	}
}
