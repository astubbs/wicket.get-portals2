/*
 * $Id$ $Revision$ $Date$
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
package wicket.util.file;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletContext;

import wicket.util.string.StringList;

/**
 * Mantains a list of folders as a path.
 * 
 * @author Johan Compagner
 */
public final class WebApplicationPath implements IResourceFinder
{
	/** The list of urls in the path */
	private final List webappPaths = new ArrayList();

	/** The list of folders in the path */
	private final List folders = new ArrayList();

	private final ServletContext servletContext;

	/**
	 * Constructor
	 * 
	 * @param servletContext
	 *            The webapplication context where the resources must be loaded
	 *            from
	 */
	public WebApplicationPath(ServletContext servletContext)
	{
		this.servletContext = servletContext;
	}

	/**
	 * @param path
	 *            add a path that is lookup through the servlet context
	 * @return The path, for invocation chaining
	 */
	public IResourceFinder add(String path)
	{
		if (!path.startsWith("/"))
		{
			path = "/" + path;
		}
		if (!path.endsWith("/"))
		{
			path += "/";
		}
		webappPaths.add(path);
		return this;
	}

	/**
	 * @param folder
	 * @return The WebApplicationResources, for invocation chaining
	 */
	public IResourceFinder add(final Folder folder)
	{
		folders.add(folder);
		return this;
	}

	/**
	 * Looks for a given pathname along this path
	 * 
	 * @param pathname
	 *            The filename with possible path
	 * @return The file located on the path
	 */
	public URL find(final String pathname)
	{
		for (final Iterator iterator = folders.iterator(); iterator.hasNext();)
		{
			Folder folder = (Folder)iterator.next();
			File file = new File(folder, pathname);
			if (file.exists())
			{
				try
				{
					return file.toURI().toURL();
				}
				catch (MalformedURLException ex)
				{
					// ignore
				}
			}
		}
		for (final Iterator iterator = webappPaths.iterator(); iterator.hasNext();)
		{
			final String path = (String)iterator.next();
			try
			{
				final URL file = servletContext.getResource(path + pathname);
				if (file != null)
				{
					return file;
				}
			}
			catch (Exception ex)
			{
				// ignore, file couldn't be found
			}
		}

		return null;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString()
	{
		return "[folders = " + StringList.valueOf(folders) + ", webapppaths: "
				+ StringList.valueOf(webappPaths) + "]";
	}
}
