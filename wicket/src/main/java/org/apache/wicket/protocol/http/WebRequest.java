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
package org.apache.wicket.protocol.http;

import java.util.Locale;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.apache.wicket.Request;
import org.apache.wicket.util.lang.Bytes;


/**
 * Subclass of Request for HTTP protocol requests which holds an underlying HttpServletRequest
 * object. A variety of convenience methods are available that operate on the HttpServletRequest
 * object. These methods do things such as providing access to parameters, cookies, URLs and path
 * information.
 * 
 * @author Jonathan Locke
 */
public abstract class WebRequest extends Request
{
	/**
	 * Get the requests' cookies
	 * 
	 * @return Cookies
	 */
	public Cookie[] getCookies()
	{
		return getHttpServletRequest().getCookies();
	}

	/**
	 * Get the requests' cookie by name
	 * 
	 * @param name
	 *            The name of the cookie to be looked up
	 * 
	 * @return A cookie, null if not found.
	 * @since 1.3.0-beta4
	 */
	public Cookie getCookie(String name)
	{
		Cookie[] cookies = getCookies();
		if (cookies != null && cookies.length > 0)
		{
			for (int i = 0; i < cookies.length; i++)
			{
				if (cookies[i].getName().equals(name))
				{
					return cookies[i];
				}
			}
		}
		return null;
	}

	/**
	 * Gets the wrapped http servlet request object.
	 * <p>
	 * WARNING: it is usually a bad idea to depend on the http servlet request directly. Please use
	 * the classes and methods that are exposed by Wicket (such as {@link org.apache.wicket.Session}
	 * instead. Send an email to the mailing list in case it is not clear how to do things or you
	 * think you miss functionality which causes you to depend on this directly.
	 * </p>
	 * 
	 * @return the wrapped http serlvet request object.
	 */
	public abstract HttpServletRequest getHttpServletRequest();

	/**
	 * Returns the preferred <code>Locale</code> that the client will accept content in, based on
	 * the Accept-Language header. If the client request doesn't provide an Accept-Language header,
	 * this method returns the default locale for the server.
	 * 
	 * @return the preferred <code>Locale</code> for the client
	 */
	@Override
	public abstract Locale getLocale();

	/**
	 * Gets the request parameter with the given key.
	 * 
	 * @param key
	 *            Parameter name
	 * @return Parameter value
	 */
	@Override
	public abstract String getParameter(final String key);

	/**
	 * Gets the request parameters.
	 * 
	 * @return Map of parameters
	 */
	@Override
	public abstract Map<String, String[]> getParameterMap();

	/**
	 * Gets the request parameters with the given key.
	 * 
	 * @param key
	 *            Parameter name
	 * @return Parameter values
	 */
	@Override
	public abstract String[] getParameters(final String key);

	/**
	 * Gets the servlet path.
	 * 
	 * @return Servlet path
	 */
	public abstract String getServletPath();

	/**
	 * Create a runtime context type specific (e.g. Servlet or Portlet) MultipartWebRequest wrapper
	 * for handling multipart content uploads.
	 * 
	 * @param maxSize
	 *            the maximum size this request may be
	 * @return new WebRequest wrapper implementing MultipartWebRequest
	 */
	public abstract WebRequest newMultipartWebRequest(Bytes maxSize);

	/**
	 * Is the request an ajax request?
	 * 
	 * @return True if the ajax is an ajax request. False if it's not.
	 */
	public abstract boolean isAjax();
}
