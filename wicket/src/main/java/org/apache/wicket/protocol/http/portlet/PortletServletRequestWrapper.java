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

import java.io.UnsupportedEncodingException;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpSession;

/**
 * Wraps servlet request object with Portal specific functionality.
 * 
 * FIXME javadoc
 * 
 * @author Ate Douma
 */
public class PortletServletRequestWrapper extends HttpServletRequestWrapper
{
	private String contextPath;
	private final String servletPath;
	private String pathInfo;
	private String requestURI;
	private String queryString;
	private HttpSession session;

	/**
	 * FIXME javadoc
	 * 
	 * @param request
	 * @param filterPath
	 * @return
	 */
	private static String decodePathInfo(HttpServletRequest request, String filterPath)
	{
		String pathInfo = request.getRequestURI().substring(
			request.getContextPath().length() + filterPath.length());
		return pathInfo == null || pathInfo.length() < 2 ? null : pathInfo;
	}

	/**
	 * FIXME javadoc
	 * 
	 * @param filterPath
	 * @return
	 */
	private static String makeServletPath(String filterPath)
	{
		return "/" + filterPath.substring(0, filterPath.length() - 1);
	}

	/**
	 * FIXME javadoc
	 * 
	 * @param context
	 * @param proxiedSession
	 * @param request
	 * @param filterPath
	 */
	protected PortletServletRequestWrapper(ServletContext context, HttpSession proxiedSession,
		HttpServletRequest request, String filterPath)
	{
		super(request);
		session = proxiedSession;
		if (proxiedSession == null)
		{
			session = request.getSession(false);
		}
		servletPath = makeServletPath(filterPath);
		// retrieve the correct contextPath, requestURI and queryString
		// if request request is an include
		if ((contextPath = (String)request.getAttribute("javax.servlet.include.context_path")) != null)
		{
			requestURI = (String)request.getAttribute("javax.servlet.include.request_uri");
			queryString = (String)request.getAttribute("javax.servlet.include.query_string");
		}
		// else if request is a forward
		else if ((contextPath = (String)request.getAttribute("javax.servlet.forward.context_path")) != null)
		{
			requestURI = (String)request.getAttribute("javax.servlet.forward.request_uri");
			queryString = (String)request.getAttribute("javax.servlet.forward.query_string");
		}
		// else it is a normal request
		else
		{
			contextPath = request.getContextPath();
			requestURI = request.getRequestURI();
			queryString = request.getQueryString();
		}
	}

	/**
	 * FIXME javadoc
	 * 
	 * @param context
	 * @param request
	 * @param proxiedSession
	 * @param filterPath
	 */
	public PortletServletRequestWrapper(ServletContext context, HttpServletRequest request,
		HttpSession proxiedSession, String filterPath)
	{
		this(context, proxiedSession, request, filterPath);

		String pathInfo = requestURI.substring(contextPath.length() + filterPath.length());
		this.pathInfo = pathInfo == null || pathInfo.length() < 2 ? null : pathInfo;
	}

	/**
	 * FIXME javadoc
	 * 
	 * @param context
	 * @param request
	 * @param proxiedSession
	 * @param filterPath
	 * @param pathInfo
	 */
	public PortletServletRequestWrapper(ServletContext context, HttpServletRequest request,
		HttpSession proxiedSession, String filterPath, String pathInfo)
	{
		this(context, proxiedSession, request, filterPath);

		this.pathInfo = pathInfo;
		// override requestURI
		requestURI = contextPath + servletPath + (pathInfo != null ? pathInfo : "");
	}

	@Override
	public String getContextPath()
	{
		return contextPath;
	}

	@Override
	public String getServletPath()
	{
		return servletPath;
	}

	@Override
	public String getPathInfo()
	{
		return pathInfo;
	}

	@Override
	public String getRequestURI()
	{
		return requestURI;
	}

	@Override
	public String getQueryString()
	{
		return queryString;
	}

	@Override
	public HttpSession getSession()
	{
		return getSession(true);
	}

	@Override
	public HttpSession getSession(boolean create)
	{
		return session != null ? session : super.getSession(create);
	}

	@Override
	public void setCharacterEncoding(String enc) throws UnsupportedEncodingException
	{
		// TODO check different lifecycles? Only allow in ACTION and RESOURCE requests?
		// see PLT.19.3.6
	}

	@Override
	public Object getAttribute(String name)
	{
		// TODO: check if these can possibly be set/handled
		// nullifying these for now to prevent Wicket
		// ServletWebRequest.getRelativePathPrefixToWicketHandler() going the wrong route
		if ("javax.servlet.error.request_uri".equals(name) ||
			"javax.servlet.forward.servlet_path".equals(name))
		{
			return null;
		}
		return super.getAttribute(name);
	}


}
