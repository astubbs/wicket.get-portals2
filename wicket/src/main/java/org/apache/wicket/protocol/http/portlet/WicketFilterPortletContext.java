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

import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletResponse;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceResponse;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.portals.bridges.util.ServletPortletSessionProxy;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.protocol.http.WebRequest;
import org.apache.wicket.protocol.http.WebResponse;
import org.apache.wicket.protocol.http.WicketFilter;
import org.apache.wicket.protocol.http.servlet.ServletWebRequest;
import org.apache.wicket.settings.IRequestCycleSettings;

/**
 * Handles Portlet specific filtering requirements.
 * 
 * @see WicketFilter
 * @author Ate Douma
 */
public class WicketFilterPortletContext
{
	private static final String SERVLET_RESOURCE_URL_PORTLET_WINDOW_ID_PREFIX = "/ps:";

	/**
	 * Overrides render strategy and adds the {@link PortletInvalidMarkupFilter} filter.
	 * 
	 * @see PortletInvalidMarkupFilter
	 * @param webApplication
	 */
	public void initFilter(WebApplication webApplication) throws ServletException
	{
		// override render strategy to REDIRECT_TO_REDNER
		webApplication.getRequestCycleSettings().setRenderStrategy(
			IRequestCycleSettings.REDIRECT_TO_RENDER);
		// Add response filter to remove extra HTML such as <body> etc as they are not appropriate
		// for portlet environments
		webApplication.getRequestCycleSettings()
			.addResponseFilter(new PortletInvalidMarkupFilter());
	}

	/**
	 * Sets up the filter to process a given request cycle. Potentially wraps the request and
	 * response objects with portlet specific wrappers.
	 * 
	 * @param config
	 *            filter configuration
	 * @param filterRequestContext
	 * @param filterPath
	 * @return true if we are in a portlet environment
	 * @throws IOException
	 * @throws ServletException
	 */
	public boolean setupFilter(FilterConfig config, FilterRequestContext filterRequestContext,
		String filterPath) throws IOException, ServletException
	{
		boolean inPortletContext = false;
		PortletConfig portletConfig = (PortletConfig)filterRequestContext.getRequest()
			.getAttribute("javax.portlet.config");
		// if we are running in a portlet environment
		if (portletConfig != null)
		{
			inPortletContext = true;
			// get response state
			WicketResponseState responseState = (WicketResponseState)filterRequestContext.getRequest()
				.getAttribute(WicketPortlet.RESPONSE_STATE_ATTR);

			// FIXME - decouple from Apache Bridges project - do we still need
			// ServletPortletSessionProxy?
			filterRequestContext.setRequest(new PortletServletRequestWrapper(
				config.getServletContext(), filterRequestContext.getRequest(),
				ServletPortletSessionProxy.createProxy(filterRequestContext.getRequest()),
				filterPath));

			// get the portlet response type
			PortletResponse responseType = (PortletResponse)filterRequestContext.getRequest()
				.getAttribute("javax.portlet.response");
			// get the portlet request type
			String requestType = (String)filterRequestContext.getRequest().getAttribute(
				WicketPortlet.REQUEST_TYPE_ATTR);

			// wrap request object in appropriate PortletServletResponseWrapper
			PortletServletResponseWrapper response;
			// Action request type
			if (WicketPortlet.ACTION_REQUEST.equals(requestType))
			{
				response = new PortletActionServletResponseWrapper(
					filterRequestContext.getResponse(), (ActionResponse)responseType, responseState);
			}
			// Resource request type
			else if (WicketPortlet.RESOURCE_REQUEST.equals(requestType))
			{
				response = new PortletResourceServletResponseWrapper(
					filterRequestContext.getResponse(), (ResourceResponse)responseType,
					responseState);
			}
			// Event request type
			else if (WicketPortlet.EVENT_REQUEST.equals(requestType))
			{
				response = new PortletRenderServletResponseWrapper(
					filterRequestContext.getResponse(), (RenderResponse)responseType, responseState);
			}
			else
			{
				// FIXME appropriate to throw exception? What about render requests?
				throw new WicketRuntimeException("Unsupported request type");
			}
			filterRequestContext.setResponse(response);
		}
		else
		{
			ServletContext context = config.getServletContext();
			HttpServletRequest request = filterRequestContext.getRequest();
			String pathInfo = request.getRequestURI().substring(
				request.getContextPath().length() + filterPath.length());
			String portletWindowId = decodePortletWindowId(pathInfo);
			if (portletWindowId != null)
			{
				HttpSession proxiedSession = ServletPortletSessionProxy.createProxy(request,
					portletWindowId);
				pathInfo = stripWindowIdFromPathInfo(pathInfo);
				filterRequestContext.setRequest(new PortletServletRequestWrapper(context, request,
					proxiedSession, filterPath, pathInfo));
			}
		}
		return inPortletContext;
	}

	public boolean createPortletRequestContext(WebRequest request, WebResponse response)
	{
		if (request.getHttpServletRequest().getAttribute("javax.portlet.config") != null)
		{
			newPortletRequestContext((ServletWebRequest)request, response);
			return true;
		}
		return false;
	}

	public String getServletResourceUrlPortletWindowIdPrefix()
	{
		return SERVLET_RESOURCE_URL_PORTLET_WINDOW_ID_PREFIX;
	}

	public String decodePortletWindowId(String pathInfo)
	{
		String portletWindowId = null;
		if (pathInfo != null && pathInfo.startsWith(getServletResourceUrlPortletWindowIdPrefix()))
		{
			int nextPath = pathInfo.indexOf('/', 1);
			if (nextPath > -1)
			{
				portletWindowId = pathInfo.substring(
					getServletResourceUrlPortletWindowIdPrefix().length(), nextPath);
			}
			else
			{
				portletWindowId = pathInfo.substring(getServletResourceUrlPortletWindowIdPrefix().length());
			}
		}
		return portletWindowId;
	}

	public String stripWindowIdFromPathInfo(String pathInfo)
	{
		if (pathInfo != null && pathInfo.startsWith(getServletResourceUrlPortletWindowIdPrefix()))
		{
			int nextPath = pathInfo.indexOf('/', 1);
			pathInfo = nextPath > -1 ? pathInfo.substring(nextPath) : null;
		}
		return pathInfo;
	}

	public String encodeWindowIdInPath(String windowId, CharSequence path)
	{
		return (getServletResourceUrlPortletWindowIdPrefix().substring(1) + windowId + "/" + path);
	}

	protected void newPortletRequestContext(ServletWebRequest request, WebResponse response)
	{
		new PortletRequestContext(this, request, response);
	}
}
