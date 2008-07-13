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
package org.apache.wicket.util.resource;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import org.apache.wicket.util.string.Strings;
import org.apache.wicket.util.time.Time;


/**
 * Base class for string resources.
 * 
 * @author Jonathan Locke
 */
public abstract class AbstractStringResourceStream extends AbstractResourceStream
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** MIME content type */
	private final String contentType;

	/** The last time this stylesheet was modified */
	private Time lastModified = null;

	/**
	 * Constructor.
	 */
	public AbstractStringResourceStream()
	{
		this("text");
	}

	/**
	 * Constructor.
	 * 
	 * @param contentType
	 *            The mime type of this resource, such as "image/jpeg" or "text/html"
	 */
	public AbstractStringResourceStream(final String contentType)
	{
		this.contentType = contentType;
	}

	/**
	 * @see org.apache.wicket.util.resource.IResourceStream#close()
	 */
	public void close() throws IOException
	{
	}

	/**
	 * @see org.apache.wicket.util.resource.IResourceStream#getContentType()
	 */
	@Override
	public String getContentType()
	{
		return contentType;
	}

	/**
	 * @see org.apache.wicket.util.resource.IResourceStream#getInputStream()
	 */
	public InputStream getInputStream() throws ResourceStreamNotFoundException
	{
		final byte[] bytes;
		if (getCharset() != null)
		{
			try
			{
				bytes = getString().getBytes(getCharset().name());
			}
			catch (UnsupportedEncodingException e)
			{
				throw new ResourceStreamNotFoundException("Could not encode resource", e);
			}
		}
		else
		{
			bytes = getString().getBytes();
		}
		return new ByteArrayInputStream(bytes);
	}

	/**
	 * @see org.apache.wicket.util.watch.IModifiable#lastModifiedTime()
	 */
	@Override
	public Time lastModifiedTime()
	{
		return lastModified;
	}

	/**
	 * @param lastModified
	 *            The lastModified to set.
	 */
	public void setLastModified(Time lastModified)
	{
		this.lastModified = lastModified;
	}

	/**
	 * @return The string resource
	 */
	protected abstract String getString();

	@Override
	public final long length()
	{
		return Strings.lengthInBytes(getString(), getCharset());
	}
}
