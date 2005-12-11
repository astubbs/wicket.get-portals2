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
package wicket.markup;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

import wicket.util.resource.IResourceStream;
import wicket.util.resource.ResourceStreamNotFoundException;
import wicket.util.time.Time;

/**
 * A IResourceStream implementation which maintains container related
 * information about the markup resource stream.
 * 
 * @author Juergen Donnerstag
 */
public class MarkupResourceStream implements IResourceStream
{
	/**  */
	private static final long serialVersionUID = 1846489965076612828L;
	
	private final IResourceStream resourceStream;
	
	private final ContainerInfo containerInfo;
	
	/** The actual component class the markup is directly associated with */
	private final Class markupClass;
	
	/**
	 * Construct.
	 * 
	 * @param resourceStream
	 * @param containerInfo
	 * @param markupClass
	 */
	public MarkupResourceStream(final IResourceStream resourceStream, final ContainerInfo containerInfo, final Class markupClass)
	{
		this.resourceStream = resourceStream;
		this.containerInfo = containerInfo;
		this.markupClass = markupClass;
		
		if (resourceStream == null)
		{
			throw new IllegalArgumentException("Parameter 'resourceStream' must not be null");
		}
	}

	/**
	 * 
	 * @see wicket.util.resource.IResourceStream#close()
	 */
	public void close() throws IOException
	{
		resourceStream.close();
	}

	/**
	 * 
	 * @see wicket.util.resource.IResourceStream#getContentType()
	 */
	public String getContentType()
	{
		return resourceStream.getContentType();
	}

	/**
	 * 
	 * @see wicket.util.resource.IResourceStream#getInputStream()
	 */
	public InputStream getInputStream() throws ResourceStreamNotFoundException
	{
		return resourceStream.getInputStream();
	}

	/**
	 * 
	 * @see wicket.util.resource.IResourceStream#getLocale()
	 */
	public Locale getLocale()
	{
		return resourceStream.getLocale();
	}

	/**
	 * 
	 * @see wicket.util.watch.IModifiable#lastModifiedTime()
	 */
	public Time lastModifiedTime()
	{
		return resourceStream.lastModifiedTime();
	}

	/**
	 * 
	 * @see wicket.util.resource.IResourceStream#length()
	 */
	public long length()
	{
		return resourceStream.length();
	}

	/**
	 * 
	 * @see wicket.util.resource.IResourceStream#setLocale(java.util.Locale)
	 */
	public void setLocale(Locale locale)
	{
		resourceStream.setLocale(locale);
	}

	/**
	 * Get the actual component class the markup is directly associated with. Note: it not necessarily
	 * must be the container class.
	 *  
	 * @return The directly associated class
	 */
	public Class getMarkupClass()
	{
		return markupClass;
	}

	/**
	 * Get the container infos associated with the markup
	 * 
	 * @return ContainerInfo
	 */
	public ContainerInfo getContainerInfo()
	{
		return containerInfo;
	}

	/**
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString()
	{
		return resourceStream.toString();
	}
}
