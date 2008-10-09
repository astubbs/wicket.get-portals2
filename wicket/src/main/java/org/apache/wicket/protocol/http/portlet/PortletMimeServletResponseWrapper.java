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
package org.apache.wicket.protocol.http.portlet;

import java.io.IOException;

import javax.portlet.MimeResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.apache.wicket.RequestContext;

/**
 * Super class providing common Portlet response functions for different response types.
 * 
 * @author Thijs Vonk
 */
public class PortletMimeServletResponseWrapper extends PortletServletResponseWrapper
{
	MimeResponse mimeResponse;

	public PortletMimeServletResponseWrapper(HttpServletResponse response,
		MimeResponse mimeResponse, WicketResponseState responseState)
	{
		super(response, responseState);
		this.mimeResponse = mimeResponse;
	}

	/**
	 * @see javax.servlet.ServletResponseWrapper#setContentType(java.lang.String)
	 */
	@Override
	public void setContentType(String arg0)
	{
		mimeResponse.setContentType(arg0);
	}

	@Override
	public void addHeader(String name, String value)
	{
		mimeResponse.addProperty(name, value);
	}

	@Override
	public void setHeader(String name, String value)
	{
		mimeResponse.setProperty(name, value);
	}

	@Override
	public void addIntHeader(String name, int value)
	{
		mimeResponse.addProperty(name, Integer.toString(value));
	}

	@Override
	public void setIntHeader(String name, int value)
	{
		mimeResponse.setProperty(name, Integer.toString(value));
	}

	@Override
	public void addDateHeader(String name, long date)
	{
		mimeResponse.addProperty(name, Long.toString(date));
	}

	@Override
	public void setDateHeader(String name, long date)
	{
		mimeResponse.setProperty(name, Long.toString(date));
	}

	@Override
	public void addCookie(Cookie cookie)
	{
		mimeResponse.addProperty(cookie);
	}


	@Override
	public void sendRedirect(String redirectLocation) throws IOException
	{
		RequestContext rc = RequestContext.get();
		if (rc instanceof PortletRequestContext)
		{
			String wicketUrl = ((PortletRequestContext)rc).getLastEncodedPath(redirectLocation);
			if (wicketUrl != null)
			{
				redirectLocation = wicketUrl;
			}
			else
			{
				String contextPath = ((PortletRequestContext)rc).getPortletRequest()
					.getContextPath();
				if (redirectLocation.startsWith(contextPath + "/"))
				{
					redirectLocation = redirectLocation.substring(contextPath.length());
					if (redirectLocation.length() == 0)
					{
						redirectLocation = "/";
					}
				}
			}
		}
		super.sendRedirect(redirectLocation);
	}

}
