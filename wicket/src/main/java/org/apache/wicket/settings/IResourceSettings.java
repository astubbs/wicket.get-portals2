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
package org.apache.wicket.settings;

import java.util.List;

import org.apache.wicket.IResourceFactory;
import org.apache.wicket.Localizer;
import org.apache.wicket.markup.html.IPackageResourceGuard;
import org.apache.wicket.markup.html.JavascriptPackageResource;
import org.apache.wicket.markup.html.PackageResourceGuard;
import org.apache.wicket.model.IModel;
import org.apache.wicket.resource.IPropertiesFactory;
import org.apache.wicket.resource.loader.IStringResourceLoader;
import org.apache.wicket.util.file.IResourceFinder;
import org.apache.wicket.util.resource.locator.IResourceStreamLocator;
import org.apache.wicket.util.time.Duration;
import org.apache.wicket.util.watch.ModificationWatcher;


/**
 * Interface for resource related settings
 * <p>
 * <i>resourcePollFrequency </i> (defaults to no polling frequency) - Frequency at which resources
 * should be polled for changes.
 * <p>
 * <i>resourceFinder </i> (classpath) - Set this to alter the search path for resources.
 * <p>
 * <i>useDefaultOnMissingResource </i> (defaults to true) - Set to true to return a default value if
 * available when a required string resource is not found. If set to false then the
 * throwExceptionOnMissingResource flag is used to determine how to behave. If no default is
 * available then this is the same as if this flag were false
 * <p>
 * <i>A ResourceStreamLocator </i>- An Application's ResourceStreamLocator is used to find resources
 * such as images or markup files. You can supply your own ResourceStreamLocator if your prefer to
 * store your application's resources in a non-standard location (such as a different filesystem
 * location, a particular JAR file or even a database) by overriding the getResourceLocator()
 * method.
 * <p>
 * <i>Resource Factories </i>- Resource factories can be used to create resources dynamically from
 * specially formatted HTML tag attribute values. For more details, see {@link IResourceFactory},
 * {@link org.apache.wicket.markup.html.image.resource.DefaultButtonImageResourceFactory} and
 * especially {@link org.apache.wicket.markup.html.image.resource.LocalizedImageResource}.
 * <p>
 * <i>A Localizer </i> The getLocalizer() method returns an object encapsulating all of the
 * functionality required to access localized resources. For many localization problems, even this
 * will not be required, as there are convenience methods available to all components:
 * {@link org.apache.wicket.Component#getString(String key)} and
 * {@link org.apache.wicket.Component#getString(String key, IModel model)}.
 * <p>
 * <i>stringResourceLoaders </i>- A chain of <code>IStringResourceLoader</code> instances that are
 * searched in order to obtain string resources used during localization. By default the chain is
 * set up to first search for resources against a particular component (e.g. page etc.) and then
 * against the application.
 * </p>
 * 
 * @author Igor Vaynberg (ivaynberg)
 */
public interface IResourceSettings
{
	/**
	 * Adds a resource factory to the list of factories to consult when generating resources
	 * automatically
	 * 
	 * @param name
	 *            The name to give to the factory
	 * @param resourceFactory
	 *            The resource factory to add
	 */
	void addResourceFactory(final String name, final IResourceFactory resourceFactory);

	/**
	 * Convenience method that sets the resource search path to a single folder. use when searching
	 * for resources. By default, the resources are located on the classpath. If you want to
	 * configure other, additional, search paths, you can use this method
	 * 
	 * @param resourceFolder
	 *            The resourceFolder to set
	 */
	void addResourceFolder(final String resourceFolder);

	/**
	 * Add a string resource loader to the chain of loaders. If this is the first call to this
	 * method since the creation of the application settings then the existing chain is cleared
	 * before the new loader is added.
	 * 
	 * @param loader
	 *            The loader to be added
	 */
	void addStringResourceLoader(final IStringResourceLoader loader);

	/**
	 * Add a string resource loader to the chain of loaders. If this is the first call to this
	 * method since the creation of the application settings then the existing chain is cleared
	 * before the new loader is added.
	 * 
	 * @param index
	 *            The position within the array to insert the loader
	 * @param loader
	 *            The loader to be added
	 */
	void addStringResourceLoader(final int index, final IStringResourceLoader loader);

	/**
	 * Whether to disable gzip compression for resources. You need this on SAP, which gzips things
	 * twice.
	 * 
	 * @return True if we should disable gzip compression
	 * @since 1.3.0
	 */
	boolean getDisableGZipCompression();

	/**
	 * Get the application's localizer.
	 * 
	 * @see IResourceSettings#addStringResourceLoader(org.apache.wicket.resource.loader.IStringResourceLoader)
	 *      for means of extending the way Wicket resolves keys to localized messages.
	 * 
	 * @return The application wide localizer instance
	 */
	Localizer getLocalizer();

	/**
	 * Gets the {@link PackageResourceGuard package resource guard}.
	 * 
	 * @return The package resource guard
	 */
	IPackageResourceGuard getPackageResourceGuard();

	/**
	 * Get the property factory which will be used to load property files
	 * 
	 * @return PropertiesFactory
	 */
	IPropertiesFactory getPropertiesFactory();

	/**
	 * @param name
	 *            Name of the factory to get
	 * @return The IResourceFactory with the given name.
	 */
	IResourceFactory getResourceFactory(final String name);

	/**
	 * Gets the resource finder to use when searching for resources.
	 * 
	 * @return Returns the resourceFinder.
	 * @see IResourceSettings#setResourceFinder(IResourceFinder)
	 */
	IResourceFinder getResourceFinder();

	/**
	 * @return Returns the resourcePollFrequency.
	 * @see IResourceSettings#setResourcePollFrequency(Duration)
	 */
	Duration getResourcePollFrequency();

	/**
	 * @return Resource locator for this application
	 */
	IResourceStreamLocator getResourceStreamLocator();

	/**
	 * @param start
	 *            boolean if the resource watcher should be started if not already started.
	 * 
	 * @return Resource watcher with polling frequency determined by setting, or null if no polling
	 *         frequency has been set.
	 */
	ModificationWatcher getResourceWatcher(boolean start);

	/**
	 * @return an unmodifiable list of all available string resource loaders
	 */
	List<IStringResourceLoader> getStringResourceLoaders();

	/**
	 * @see org.apache.wicket.settings.IExceptionSettings#getThrowExceptionOnMissingResource()
	 * 
	 * @return boolean
	 */
	boolean getThrowExceptionOnMissingResource();

	/**
	 * @return Whether to use a default value (if available) when a missing resource is requested
	 */
	boolean getUseDefaultOnMissingResource();

	/**
	 * Sets whether to disable gzip compression for resources. You need to set this on some SAP
	 * versions, which gzip things twice.
	 * 
	 * @param disableGZipCompression
	 * @since 1.3.0
	 */
	void setDisableGZipCompression(final boolean disableGZipCompression);

	/**
	 * Sets the localizer which will be used to find property values.
	 * 
	 * @param localizer
	 * @since 1.3.0
	 */
	void setLocalizer(Localizer localizer);

	/**
	 * Sets the {@link PackageResourceGuard package resource guard}.
	 * 
	 * @param packageResourceGuard
	 *            The package resource guard
	 */
	void setPackageResourceGuard(IPackageResourceGuard packageResourceGuard);

	/**
	 * Set the property factory which will be used to load property files
	 * 
	 * @param factory
	 */
	void setPropertiesFactory(IPropertiesFactory factory);

	/**
	 * Sets the finder to use when searching for resources. By default, the resources are located on
	 * the classpath. If you want to configure other, additional, search paths, you can use this
	 * method.
	 * 
	 * @param resourceFinder
	 *            The resourceFinder to set
	 */
	void setResourceFinder(final IResourceFinder resourceFinder);

	/**
	 * Sets the resource polling frequency. This is the duration of time between checks of resource
	 * modification times. If a resource, such as an HTML file, has changed, it will be reloaded.
	 * Default is for no resource polling to occur.
	 * 
	 * @param resourcePollFrequency
	 *            Frequency at which to poll resources
	 * @see IResourceSettings#setResourceFinder(IResourceFinder)
	 */
	void setResourcePollFrequency(final Duration resourcePollFrequency);

	/**
	 * Sets the resource stream locator for this application
	 * 
	 * @param resourceStreamLocator
	 *            new resource stream locator
	 */
	void setResourceStreamLocator(IResourceStreamLocator resourceStreamLocator);

	/**
	 * @see org.apache.wicket.settings.IExceptionSettings#setThrowExceptionOnMissingResource(boolean)
	 * 
	 * @param throwExceptionOnMissingResource
	 */
	void setThrowExceptionOnMissingResource(final boolean throwExceptionOnMissingResource);

	/**
	 * @param useDefaultOnMissingResource
	 *            Whether to use a default value (if available) when a missing resource is requested
	 */
	void setUseDefaultOnMissingResource(final boolean useDefaultOnMissingResource);

	/**
	 * Sets whether the whitespace characters and comments should be stripped for resources served
	 * through {@link JavascriptPackageResource}
	 * 
	 * @param value
	 */
	void setStripJavascriptCommentsAndWhitespace(boolean value);

	/**
	 * @return whether the comments and whitespace characters will be stripped from resources served
	 *         through {@link JavascriptPackageResource}
	 */
	boolean getStripJavascriptCommentsAndWhitespace();

	/**
	 * Sets whether Wicket should add last modified time as a parameter to resource reference URL
	 * (can help with browsers too aggressively caching certain resources).
	 * 
	 * @param value
	 */
	public void setAddLastModifiedTimeToResourceReferenceUrl(boolean value);

	/**
	 * Returns whether Wicket should add last modified time as resource reference URL parameter.
	 * 
	 * @return whether Wicket should add last modified time as resource reference URL parameter
	 */
	public boolean getAddLastModifiedTimeToResourceReferenceUrl();


	/**
	 * placeholder string for '..' within resource urls (which will be crippled by the browser and
	 * not work anymore)
	 * 
	 * @return placeholder
	 */
	CharSequence getParentFolderPlaceholder();

	/**
	 * set placeholder for '..' inside resource urls
	 * 
	 * @see #getParentFolderPlaceholder()
	 * 
	 * @param sequence
	 *            character sequence which must not be ambiguous within urls
	 */
	void setParentFolderPlaceholder(CharSequence sequence);
}
