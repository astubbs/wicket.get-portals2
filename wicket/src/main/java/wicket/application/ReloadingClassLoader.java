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
package wicket.application;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import wicket.util.file.File;
import wicket.util.listener.IChangeListener;
import wicket.util.time.Duration;
import wicket.util.watch.ModificationWatcher;

/**
 * Custom ClassLoader that reverses the classloader lookups, and that is able to
 * notify a listener when a class file is changed.
 */
public class ReloadingClassLoader extends URLClassLoader
{
	private IChangeListener listener;
	private Duration pollFrequency = Duration.seconds(3);
	private static final Log log = LogFactory.getLog(ReloadingClassLoader.class);
	private static List urls = new ArrayList();
	private ModificationWatcher watcher;
	private static boolean enabled = false;

	/**
	 * Add the location of a directory containing class files 
	 * @param url the URL for the directory
	 */
	public static void addLocation(URL url) {
		urls.add(url);
	}

	/**
	 * Returns the list of all configured locations of directories containing class files
	 * @return list of locations as URL
	 */
	public static List getLocations() {
		return urls;
	}

	/**
	 * Create a new reloading ClassLoader from a list of URLs, and initialize
	 * the ModificationWatcher to detect class file modifications
	 * 
	 * @param urls
	 *            the list of URLs where to lookup class files
	 * @param parent
	 *            the parent classloader in case the class file cannot be loaded
	 *            from the above locations
	 */
	public ReloadingClassLoader(ClassLoader parent)
	{
		super((URL[])urls.toArray(new URL[0]), parent);
		this.watcher = new ModificationWatcher(pollFrequency);
	}

	/**
	 * Loads the class from this <code>ClassLoader</class>.  If the
	 * class does not exist in this one, we check the parent.  Please
	 * note that this is the exact opposite of the
	 * <code>ClassLoader</code> spec.  We use it to load the class
	 * from the same classloader as WicketFilter or WicketServlet.
	 * When found, the class file is watched for modifications.
	 *
	 * @param     name the name of the class
	 * @param     resolve if <code>true</code> then resolve the class
	 * @return    the resulting <code>Class</code> object
	 * @exception ClassNotFoundException if the class could not be found
	 */
	public final Class loadClass(String name, boolean resolve) throws ClassNotFoundException
	{
		// First check if it's already loaded
		Class clazz = findLoadedClass(name);

		if (clazz == null)
		{

			final ClassLoader parent = getParent();

			try
			{
				clazz = findClass(name);
				watchForModifications(clazz);
			}
			catch (ClassNotFoundException cnfe)
			{
				if (parent == null)
				{
					// Propagate exception
					throw cnfe;
				}
			}

			if (clazz == null)
			{
				if (parent == null)
				{
					throw new ClassNotFoundException(name);
				}
				else
				{
					// Will throw a CFNE if not found in parent
					clazz = parent.loadClass(name);
				}
			}
		}

		if (resolve)
		{
			resolveClass(clazz);
		}

		return clazz;
	}

	/**
	 * Watch changes of a class file by locating it in the list of location URLs and adding the corresponding file to the ModificationWatcher
	 *
	 * @param clz the class to watch
	 */
	void watchForModifications(Class clz)
	{
		// Watch class in the future
		Iterator locationsIterator = urls.iterator();
		File clzFile = null;
		while (locationsIterator.hasNext())
		{
			URL location = (URL)locationsIterator.next();
			String clzLocation = location.getFile() + clz.getName().replaceAll("\\.", "/")
					+ ".class";
			log.debug("clzLocation=" + clzLocation);
			clzFile = new File(clzLocation);
			final File finalClzFile = clzFile;
			if (clzFile.exists())
			{
				log.info("Watching changes of class " + clzFile);
				watcher.add(clzFile, new IChangeListener()
				{
					public void onChange()
					{
						log.info("Class file " + finalClzFile + " has changed, reloading");
						try
						{
							listener.onChange();
						}
						finally
						{
							// If an error occurs when the listener is notified, remove
							// the watched object to avoid rethrowing the exception at next check
							// FIXME check if class file has been deleted
							watcher.remove(finalClzFile);
						}
					}
				});
				break;
			}
			else
			{
				log.debug("Class file does not exist: " + clzFile);
			}
		}
		if (clzFile != null && !clzFile.exists())
		{
			log.debug("Could not locate class " + clz.getName());
		}
	}

	/**
	 * Sets the listener that will be notified when a class changes
	 * @param listener the listener to notify upon class change
	 */
	public void setListener(IChangeListener listener)
	{
		this.listener = listener;
	}
}
