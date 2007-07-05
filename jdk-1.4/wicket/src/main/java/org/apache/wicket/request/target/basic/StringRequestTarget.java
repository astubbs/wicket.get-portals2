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
package org.apache.wicket.request.target.basic;

import java.io.OutputStream;

import org.apache.wicket.IRequestTarget;
import org.apache.wicket.RequestCycle;
import org.apache.wicket.Response;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.util.io.Streams;
import org.apache.wicket.util.resource.StringBufferResourceStream;


/**
 * Request target that responds by sending its string property.
 * 
 * @author Eelco Hillenius
 */
public class StringRequestTarget implements IRequestTarget
{
	/** the string for the response. */
	private final String string;

	/**
	 * Construct.
	 * 
	 * @param string
	 *            the string for the response
	 */
	public StringRequestTarget(String string)
	{
		if (string == null)
		{
			throw new IllegalArgumentException("Argument string must be not null");
		}

		this.string = string;
	}

	/**
	 * Responds by sending the string property.
	 * 
	 * @see org.apache.wicket.IRequestTarget#respond(org.apache.wicket.RequestCycle)
	 */
	public void respond(RequestCycle requestCycle)
	{
		// Get servlet response to use when responding with resource
		final Response response = requestCycle.getResponse();
		final StringBufferResourceStream stream = new StringBufferResourceStream();
		stream.append(string);

		// Respond with resource
		try
		{
			final OutputStream out = response.getOutputStream();
			try
			{
				Streams.copy(stream.getInputStream(), out);
			}
			finally
			{
				stream.close();
				out.flush();
			}
		}
		catch (Exception e)
		{
			throw new WicketRuntimeException("Unable to render resource stream " + stream, e);
		}
	}

	/**
	 * @see org.apache.wicket.IRequestTarget#detach(org.apache.wicket.RequestCycle)
	 */
	public void detach(RequestCycle requestCycle)
	{
	}

	/**
	 * Gets the string property.
	 * 
	 * @return the string property
	 */
	public String getString()
	{
		return string;
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj)
	{
		if (obj instanceof StringRequestTarget)
		{
			StringRequestTarget that = (StringRequestTarget)obj;
			return string.equals(that.string);
		}
		return false;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode()
	{
		int result = "StringRequestTarget".hashCode();
		result += string.hashCode();
		return 17 * result;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString()
	{
		return "[StringRequestTarget@" + hashCode() + " " + string + "]";
	}
}
