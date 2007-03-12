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
package wicket.protocol.http.servlet;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import wicket.Application;
import wicket.IRedirectListener;
import wicket.RequestListenerInterface;
import wicket.WicketRuntimeException;
import wicket.protocol.http.WebApplication;
import wicket.protocol.http.WebRequest;
import wicket.util.lang.Bytes;
import wicket.util.string.StringValueConversionException;
import wicket.util.string.Strings;
import wicket.util.upload.FileUploadException;

/**
 * A Servlet specific WebRequest implementation wrapping a HttpServletRequest
 * 
 * @author Ate Douma
 */
public class ServletWebRequest extends WebRequest
{
	/** Log */
	private static final Log log = LogFactory.getLog(ServletWebRequest.class);

	/** Servlet request information. */
	private final HttpServletRequest httpServletRequest;

	/**
	 * Protected constructor.
	 * 
	 * @param httpServletRequest
	 *            The servlet request information
	 */
	public ServletWebRequest(final HttpServletRequest httpServletRequest)
	{
		this.httpServletRequest = httpServletRequest;
	}

	/**
	 * Gets the servlet context path.
	 * 
	 * @return Servlet context path
	 */
	public String getContextPath()
	{
		return httpServletRequest.getContextPath();
	}

	/**
	 * Gets the wrapped http servlet request object.
	 * 
	 * @return the wrapped http serlvet request object.
	 */
	public final HttpServletRequest getHttpServletRequest()
	{
		return httpServletRequest;
	}

	/**
	 * Returns the preferred <code>Locale</code> that the client will accept
	 * content in, based on the Accept-Language header. If the client request
	 * doesn't provide an Accept-Language header, this method returns the
	 * default locale for the server.
	 * 
	 * @return the preferred <code>Locale</code> for the client
	 */
	public Locale getLocale()
	{
		return httpServletRequest.getLocale();
	}

	/**
	 * Gets the request parameter with the given key.
	 * 
	 * @param key
	 *            Parameter name
	 * @return Parameter value
	 */
	public String getParameter(final String key)
	{
		return httpServletRequest.getParameter(key);
	}

	/**
	 * Gets the request parameters.
	 * 
	 * @return Map of parameters
	 */
	public Map getParameterMap()
	{
		// return a mutable copy
		return new HashMap(httpServletRequest.getParameterMap());
	}

	/**
	 * Gets the request parameters with the given key.
	 * 
	 * @param key
	 *            Parameter name
	 * @return Parameter values
	 */
	public String[] getParameters(final String key)
	{
		return httpServletRequest.getParameterValues(key);
	}

	/**
	 * Gets the path info if any.
	 * 
	 * @return Any servlet path info
	 */
	public String getPath()
	{
		return ((WebApplication)Application.get()).getWicketFilter().getRelativePath(
				httpServletRequest);
	}

	/**
	 * Gets the relative url (url without the context path and without a leading
	 * '/'). Use this method to load resources using the servlet context.
	 * 
	 * @return Request URL
	 */
	public String getRelativeURL()
	{
		/*
		 * Servlet 2.3 specification :
		 * 
		 * Servlet Path: The path section that directly corresponds to the
		 * mapping which activated this request. This path starts with a "/"
		 * character except in the case where the request is matched with the
		 * "/*" pattern, in which case it is the empty string.
		 * 
		 * PathInfo: The part of the request path that is not part of the
		 * Context Path or the Servlet Path. It is either null if there is no
		 * extra path, or is a string with a leading "/".
		 */
		String url = getServletPath();
		final String pathInfo = httpServletRequest.getPathInfo();

		if (pathInfo != null)
		{
			url += pathInfo;
		}

		final String queryString = httpServletRequest.getQueryString();

		if (queryString != null)
		{
			url += ("?" + queryString);
		}

		// If url is non-empty it has to start with '/', which we should lose
		if (!url.equals(""))
		{
			// Remove leading '/'
			url = url.substring(1);
		}
		return url;
	}

	/**
	 * Gets the servlet path.
	 * 
	 * @return Servlet path
	 */
	public String getServletPath()
	{
		return httpServletRequest.getServletPath();
	}

	/**
	 * This will return true if the header "Wicket-Ajax" is set.
	 * 
	 * @see wicket.protocol.http.WebRequest#isAjax()
	 */
	// TODO matej? should we have a simple way of supporting other ajax things?
	// or should they just set that same header??
	public boolean isAjax()
	{
		boolean ajax = false;

		String ajaxHeader = httpServletRequest.getHeader("Wicket-Ajax");
		if (Strings.isEmpty(ajaxHeader) == false)
		{
			try
			{
				ajax = Strings.isTrue(ajaxHeader);
			}
			catch (StringValueConversionException e)
			{
				// We are not interested in this exception but we log it anyway
				log.debug("Couldn't convert the Wicket-Ajax header: " + ajaxHeader);
			}
		}

		return ajax;
	}

	/**
	 * This method by default calls isAjax(), wicket ajax request do have an
	 * header set. And for all the ajax request the versioning should be merged
	 * with the previous one. And when it sees that the current request is a
	 * redirect to page request the version will also be merged with the
	 * previous one because refresh in the browser or redirects to a page
	 * shouldn't generate a new version.
	 * 
	 * @see wicket.Request#mergeVersion()
	 */
	public boolean mergeVersion()
	{
		RequestListenerInterface intface = getRequestParameters().getInterface();
		return isAjax() || intface == IRedirectListener.INTERFACE;
	}

	/**
	 * @see wicket.protocol.http.WebRequest#newMultipartWebRequest(wicket.util.lang.Bytes)
	 */
	public WebRequest newMultipartWebRequest(Bytes maxsize)
	{
		try
		{
			return new MultipartServletWebRequest(httpServletRequest, maxsize);
		}
		catch (FileUploadException e)
		{
			throw new WicketRuntimeException(e);
		}
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString()
	{
		return "[method = " + httpServletRequest.getMethod() + ", protocol = "
				+ httpServletRequest.getProtocol() + ", requestURL = "
				+ httpServletRequest.getRequestURL() + ", contentType = "
				+ httpServletRequest.getContentType() + ", contentLength = "
				+ httpServletRequest.getContentLength() + ", contextPath = "
				+ httpServletRequest.getContextPath() + ", pathInfo = "
				+ httpServletRequest.getPathInfo() + ", requestURI = "
				+ httpServletRequest.getRequestURI() + ", servletPath = "
				+ httpServletRequest.getServletPath() + ", pathTranslated = "
				+ httpServletRequest.getPathTranslated() + "]";
	}
}